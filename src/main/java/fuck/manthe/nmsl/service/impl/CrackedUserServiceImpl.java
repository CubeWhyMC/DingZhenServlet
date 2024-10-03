package fuck.manthe.nmsl.service.impl;

import cn.hutool.crypto.SecureUtil;
import fuck.manthe.nmsl.entity.CrackedUser;
import fuck.manthe.nmsl.repository.UserRepository;
import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.service.CrackedUserService;
import fuck.manthe.nmsl.service.RedeemService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class CrackedUserServiceImpl implements CrackedUserService {
    @Resource
    UserRepository userRepository;

    @Resource
    AnalysisService analysisService;

    @Resource
    RedeemService redeemService;

    @Override
    public boolean isValid(String username, String password) {
        return isValidHash(username, SecureUtil.sha1(password));
    }

    @Override
    public boolean isValidHash(String username, String password) {
        Optional<CrackedUser> optional = userRepository.findByUsername(username);
        if (optional.isPresent()) {
            CrackedUser user = optional.get();
            return user.getPassword().equals(password);
        }
        return false;
    }

    @Override
    public boolean addUser(CrackedUser user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return false;
        }
        analysisService.userRegistered();
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public void removeUser(String username) {
        // delete user
        userRepository.deleteByUsername(username);
        // delete redeemed codes
        redeemService.deleteByRedeemer(username);
    }

    @Override
    public void removeUser(CrackedUser user) {
        userRepository.delete(user);
    }

    @Override
    public boolean renew(String username, int days) {
        Optional<CrackedUser> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) return false;
        CrackedUser user = optional.get();
        renew(user, days);
        return true;
    }

    @Override
    public void renew(CrackedUser user, int days) {
        if (days == -1) {
            user.setExpire(-1L);
        } else {
            if (user.getExpire() == -1L) {
                user.setExpire(0L);
            } else if (user.getExpire() < System.currentTimeMillis()) {
                user.setExpire(System.currentTimeMillis());
            }
            user.setExpire(user.getExpire() + (long) days * 24 * 60 * 60 * 1000);
        }
        userRepository.save(user);
        log.info("Renewed user {} (Expire at {})", user.getUsername(), user.getExpire());
    }

    @Override
    public void renewAll(int days) {
        log.info("Adding {} days to all users", days);
        for (CrackedUser crackedUser : list()) {
            if (crackedUser.getExpire() == -1) continue;
            renew(crackedUser, days);
        }
    }

    @Override
    public boolean hasExpired(String username) {
        Optional<CrackedUser> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) return true;
        if (optional.get().getExpire() == -1L) {
            return false;
        } else
            return System.currentTimeMillis() > optional.get().getExpire();
    }

    @Override
    public boolean resetPassword(String username, String newPassword) {
        Optional<CrackedUser> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) return false;
        CrackedUser user = optional.get();
        user.setPassword(SecureUtil.sha1(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public List<CrackedUser> list() {
        return userRepository.findAll();
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public void removeExpired() {
        list().stream().filter((user) -> (user.getExpire() < System.currentTimeMillis() && user.getExpire() != -1)).forEach(this::removeUser);
    }

    @Override
    public CrackedUser findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
