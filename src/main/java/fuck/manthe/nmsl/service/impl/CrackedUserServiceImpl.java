package fuck.manthe.nmsl.service.impl;

import cn.hutool.crypto.SecureUtil;
import fuck.manthe.nmsl.entity.CrackedUser;
import fuck.manthe.nmsl.repository.UserRepository;
import fuck.manthe.nmsl.service.CrackedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrackedUserServiceImpl implements CrackedUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String username, String password) {
        Optional<CrackedUser> optional = userRepository.findByUsername(username);
        if (optional.isPresent()) {
            CrackedUser user = optional.get();
            return user.getPassword().equals(SecureUtil.sha1(password));
        }
        return false;
    }

    @Override
    public boolean addUser(CrackedUser user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    @Override
    public void removeUser(String username) {
        Optional<CrackedUser> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) return;
        userRepository.delete(optional.get());
    }

    @Override
    public boolean renewUser(String username, int day) {
        Optional<CrackedUser> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) return false;
        CrackedUser user = optional.get();
        if (day == -1) {
            user.setExpire(-1L);
        } else {
            if (user.getExpire() == -1L) {
                user.setExpire(0L);
            }
            user.setExpire(user.getExpire() + (long) day * 24 * 60 * 60 * 1000);
        }
        userRepository.save(user);
        return true;
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
}
