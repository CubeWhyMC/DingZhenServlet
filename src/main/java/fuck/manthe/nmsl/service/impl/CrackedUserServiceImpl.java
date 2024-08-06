package fuck.manthe.nmsl.service.impl;

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
            return user.getPassword().equals(password);
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
}
