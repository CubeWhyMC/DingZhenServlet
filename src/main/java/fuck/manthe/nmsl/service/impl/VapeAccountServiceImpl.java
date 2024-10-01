package fuck.manthe.nmsl.service.impl;

import cn.hutool.core.util.RandomUtil;
import fuck.manthe.nmsl.entity.VapeAccount;
import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;
import fuck.manthe.nmsl.repository.VapeAccountRepository;
import fuck.manthe.nmsl.service.VapeAccountService;
import fuck.manthe.nmsl.utils.Const;
import fuck.manthe.nmsl.utils.CryptUtil;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@Log4j2
public class VapeAccountServiceImpl implements VapeAccountService {
    @Value("${share.cold-down.pre-account.during-max}")
    int coldDownMax;

    @Value("${share.cold-down.pre-account.during-min}")
    int coldDownMin;

    @Value("${share.cold-down.global.during-max}")
    int globalMaxColdDown;

    @Value("${share.cold-down.global.during-min}")
    int globalMinColdDown;


    @Resource
    RedisTemplate<String, Long> redisTemplate;

    @Resource
    RedisTemplate<String, Boolean> booleanRedisTemplate;

    @Resource
    VapeAccountRepository vapeAccountRepository;

    @Resource
    CryptUtil cryptUtil;

    @Resource
    OkHttpClient httpClient;

    @Value("${share.cold-down.global.enabled}")
    boolean coldDownEnabled;

    @Override
    public VapeAccount getOne() {
        List<VapeAccount> all = vapeAccountRepository.findAll();
        for (VapeAccount account : all) {
            if (!isColdDown(account)) {
                log.info("Shared account {} is available", account.getUsername());
                markColdDown(account);
                return account;
            }
        }
        return null; // every account is in cold down or no account
    }

    @Override
    public boolean isColdDown(VapeAccount account) {
        return getColdDown(account) > System.currentTimeMillis();
    }

    @Override
    public void markColdDown(VapeAccount account) {
        log.info("Mark shared account {} cold down", account.getUsername());
        redisTemplate.opsForValue().set(Const.ACCOUNT_COLD_DOWN + account.getId(), System.currentTimeMillis() + (long) RandomUtil.randomInt(coldDownMin, coldDownMax, true, true) * 60 * 1000);
    }

    @Override
    public void resetColdDown(VapeAccount account) {
        if (!isColdDown(account)) {
            return;
        }
        log.info("Reset shared account {} cold down", account.getUsername());
        redisTemplate.opsForValue().set(Const.ACCOUNT_COLD_DOWN + account.getId(), System.currentTimeMillis());
    }

    @Override
    public void pauseInject(boolean state) {
        booleanRedisTemplate.opsForValue().set(Const.PAUSE_INJECT, !state);
    }

    @Override
    public boolean isPaused() {
        return Boolean.TRUE.equals(booleanRedisTemplate.opsForValue().get(Const.PAUSE_INJECT));
    }

    @Override
    public boolean addAccount(VapeAccount account) {
        if (vapeAccountRepository.existsByUsername(account.getUsername())) return false;
        vapeAccountRepository.save(account);
        return true;
    }

    @Override
    @Transactional
    public boolean removeAccount(String username) {
        if (!vapeAccountRepository.existsByUsername(username)) {
            return false;
        }
        vapeAccountRepository.deleteByUsername(username);
        return true;
    }

    @Override
    public boolean updatePassword(String username, String newPassword) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        VapeAccount account = vapeAccountRepository.findByUsername(username);
        if (account == null) return false;
        account.setPassword(cryptUtil.encryptString(newPassword));
        vapeAccountRepository.save(account);
        return false;
    }

    @Override
    public boolean updateHwid(String username, String newHwid) {
        VapeAccount account = vapeAccountRepository.findByUsername(username);
        if (account == null) return false;
        account.setHwid(newHwid);
        vapeAccountRepository.save(account);
        return false;
    }

    @Override
    public List<VapeAccount> listAccounts() {
        return vapeAccountRepository.findAll();
    }

    @Override
    public long getColdDown(VapeAccount account) {
        Long value = redisTemplate.opsForValue().get(Const.ACCOUNT_COLD_DOWN + account.getId());
        if (value == null) {
            return 0;
        }
        return value;
    }

    @Override
    public Long nextAvailable() {
        long min = 0L;
        for (VapeAccount account : listAccounts()) {
            if (!isColdDown(account)) {
                return System.currentTimeMillis();
            }
            long coldDown = getColdDown(account);
            if (min == 0) {
                min = coldDown;
            } else if (coldDown < min) {
                min = coldDown;
            }
        }
        return min;
    }

    @Override
    public VapeAuthorizeDTO doAuth(VapeAccount vapeAccount) {
        try (Response response = httpClient.newCall(new Request.Builder().post(okhttp3.RequestBody.create("email=" + vapeAccount.getUsername() + "&password=" + vapeAccount.getPassword() + "&hwid=" + vapeAccount.getHwid() + "&v=v3&t=true", MediaType.parse("application/x-www-form-urlencoded"))).url("http://www.vape.gg/auth.php").header("User-Agent", "Agent_" + vapeAccount.getHwid()).build()).execute()) {
            if (response.body() != null) {
                String responseString = response.body().string();
                if (response.isSuccessful()) {
                    if (coldDownEnabled) {
                        redisTemplate.opsForValue().set(Const.COLD_DOWN, System.currentTimeMillis() + (long) RandomUtil.randomInt(globalMinColdDown, globalMaxColdDown, true, true) * 60 * 1000);
                    }
                    if (responseString.length() != 33) {
                        // not a valid token
                        log.error("Account {} seems banned or incorrect ({})", vapeAccount.getUsername(), responseString);
                        return VapeAuthorizeDTO.builder()
                                .token(responseString)
                                .status(VapeAuthorizeDTO.Status.BANNED_OR_INCORRECT)
                                .build();
                    }
                }
                return VapeAuthorizeDTO.builder()
                        .token(responseString)
                        .status(VapeAuthorizeDTO.Status.OK)
                        .build();
            } else {
                return VapeAuthorizeDTO.builder()
                        .token("Empty auth data")
                        .status(VapeAuthorizeDTO.Status.SERVLET_ERROR)
                        .build();
            }
        } catch (Exception e) {
            return VapeAuthorizeDTO.builder()
                    .token(e.getMessage())
                    .status(VapeAuthorizeDTO.Status.SERVLET_ERROR)
                    .build();
        }
    }

    @Override
    public VapeAccount findByUsername(String username) {
        return vapeAccountRepository.findByUsername(username);
    }
}
