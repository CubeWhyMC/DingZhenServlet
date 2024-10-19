package fuck.manthe.nmsl.service.impl;

import cn.hutool.core.util.RandomUtil;
import fuck.manthe.nmsl.entity.VapeAccount;
import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;
import fuck.manthe.nmsl.entity.webhook.InvalidTokenMessage;
import fuck.manthe.nmsl.repository.VapeAccountRepository;
import fuck.manthe.nmsl.service.VapeAccountService;
import fuck.manthe.nmsl.service.WebhookService;
import fuck.manthe.nmsl.util.Const;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    @Value("${share.cold-down.global.enabled}")
    boolean globalColdDownEnabled;


    @Resource
    RedisTemplate<String, Long> redisTemplate;

    @Resource
    VapeAccountRepository vapeAccountRepository;

    @Resource
    WebhookService webhookService;

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
    public boolean addAccount(VapeAccount account) {
        if (vapeAccountRepository.existsByUsername(account.getUsername())) return false;
        vapeAccountRepository.save(account);
        return true;
    }

    @Override
    public boolean removeAccount(String username) {
        if (!vapeAccountRepository.existsByUsername(username)) {
            return false;
        }
        vapeAccountRepository.deleteByUsername(username);
        return true;
    }

    @Override
    public boolean updatePassword(String username, String newPassword) {
        VapeAccount account = vapeAccountRepository.findByUsername(username);
        if (account == null) return false;
        account.setPassword(newPassword);
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
                        InvalidTokenMessage message = new InvalidTokenMessage();
                        message.setErrorCode(responseString);
                        message.setTimestamp(System.currentTimeMillis() / 1000L);
                        VapeAuthorizeDTO dto;
                        // not a valid token
                        if (responseString.equals("1006")) {
                            // Cloudflare captcha
                            message.setContent("共享账户无法进行登录,是IP被封禁了吗");
                            log.error("Your IP address was banned by Manthe. Please switch your VPN endpoint or use a Gateway. ({})", responseString);
                            dto = VapeAuthorizeDTO.builder()
                                    .token("Cloudflare")
                                    .status(VapeAuthorizeDTO.Status.CLOUDFLARE)
                                    .build();
                        } else if (responseString.equals("102")) {
                            message.setContent(String.format("共享账户账户似乎被封禁了 (内部ID: %s)", vapeAccount.getId()));
                            log.error("Vape account {} was banned. ({})", vapeAccount.getUsername(), responseString);
                            dto = VapeAuthorizeDTO.builder()
                                    .token("Disabled")
                                    .status(VapeAuthorizeDTO.Status.BANNED)
                                    .build();
                        } else {
                            message.setContent(String.format("共享账户在验证的时候产生了未知错误 (错误码: %s, 内部ID: %s)", responseString, vapeAccount.getId()));
                            log.error("Auth server responded an error: {} (Account: {})", responseString, vapeAccount.getUsername());
                            dto = VapeAuthorizeDTO.builder()
                                    .token(responseString)
                                    .status(VapeAuthorizeDTO.Status.INCORRECT)
                                    .build();
                        }
                        webhookService.pushAll("invalid-token", message);
                        return dto;
                    }
                }
                log.debug("Fetch token for {} ({})", vapeAccount.getUsername(), responseString);
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

    @Override
    public boolean hasConfigured() {
        return vapeAccountRepository.count() != 0;
    }

    @Override
    public long calculateColdDown() {
        Long next = this.nextAvailable();
        if (globalColdDownEnabled) {
            long globalColdDown = Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.COLD_DOWN), 0L);
            if (next < globalColdDown) {
                next = globalColdDown;
            }
        }
        return next;
    }
}
