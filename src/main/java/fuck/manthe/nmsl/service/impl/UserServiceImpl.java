package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.entity.User;
import fuck.manthe.nmsl.repository.UserRepository;
import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.service.OnlineConfigService;
import fuck.manthe.nmsl.service.RedeemService;
import fuck.manthe.nmsl.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserRepository userRepository;

    @Resource
    AnalysisService analysisService;

    @Resource
    RedeemService redeemService;

    @Resource
    OnlineConfigService onlineConfigService;

    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    public boolean isValid(String username, String password) {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isPresent()) {
            User user = optional.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    @Override
    public User addUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return null;
        }
        analysisService.userRegistered();
        return userRepository.save(user);
    }

    @Override
    public void removeUser(String username) {
        User user = findByUsername(username);

        // delete configs
        onlineConfigService.deleteAllCheatProfile(user);
        // delete redeemed codes
        redeemService.deleteAllByRedeemer(user);
        // delete user
        userRepository.delete(user);
        log.info("User {} was success removed.", username);
    }

    @Override
    public void removeUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public boolean renew(String username, int days) {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) return false;
        User user = optional.get();
        renew(user, days);
        return true;
    }

    @Override
    public void renew(User user, int days) {
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
        for (User crackedUser : list()) {
            if (crackedUser.getExpire() == -1) continue;
            renew(crackedUser, days);
        }
    }

    @Override
    public boolean hasExpired(String username) {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) return true;
        if (optional.get().getExpire() == -1L) {
            return false;
        } else
            return System.currentTimeMillis() > optional.get().getExpire();
    }

    @Override
    public boolean resetPassword(String username, String newPassword) {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) return false;
        User user = optional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public List<User> list() {
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
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
