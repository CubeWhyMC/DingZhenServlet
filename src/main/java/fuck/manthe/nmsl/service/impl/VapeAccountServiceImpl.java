package fuck.manthe.nmsl.service.impl;

import cn.hutool.core.util.RandomUtil;
import fuck.manthe.nmsl.entity.VapeAccount;
import fuck.manthe.nmsl.repository.VapeAccountRepository;
import fuck.manthe.nmsl.service.VapeAccountService;
import fuck.manthe.nmsl.utils.Const;
import fuck.manthe.nmsl.utils.CryptUtil;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
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
public class VapeAccountServiceImpl implements VapeAccountService {
    @Value("${share.cold-down.pre-account.during-max}")
    int coldDownMax;
    @Value("${share.cold-down.pre-account.during-min}")
    int coldDownMin;

    @Resource
    RedisTemplate<String, Long> redisTemplate;
    @Resource
    VapeAccountRepository vapeAccountRepository;
    @Resource
    CryptUtil cryptUtil;

    @Override
    public VapeAccount getOne() {
        List<VapeAccount> all = vapeAccountRepository.findAll();
        for (VapeAccount account : all) {
            if (!isColdDown(account)) {
                markColdDown(account);
                return account;
            }
        }
        return null; // every account is in cold down or no account
    }

    @Override
    public boolean isColdDown(VapeAccount account) {
        Long value = redisTemplate.opsForValue().get(Const.ACCOUNT_COLD_DOWN + account.getId());
        if (value == null) {
            return false;
        }
        return value < System.currentTimeMillis();
    }

    @Override
    public void markColdDown(VapeAccount account) {
        redisTemplate.opsForValue().set(Const.ACCOUNT_COLD_DOWN + account.getId(), System.currentTimeMillis() + (long) RandomUtil.randomInt(coldDownMin, coldDownMax, true, true) * 60 * 1000);
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
}
