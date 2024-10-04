package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.entity.OnlineToken;
import fuck.manthe.nmsl.entity.User;
import fuck.manthe.nmsl.repository.OnlineTokenRepository;
import fuck.manthe.nmsl.service.OnlineConfigService;
import fuck.manthe.nmsl.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class OnlineConfigServiceImpl implements OnlineConfigService {
    @Resource
    OnlineTokenRepository onlineTokenRepository;

    @Resource
    UserService userService;

    @Override
    public void cache(String token, String username) {
        onlineTokenRepository.save(OnlineToken.builder()
                .token(token)
                .username(username)
                .build());
    }

    @Override
    public User findByToken(String token) {
        OnlineToken onlineToken = onlineTokenRepository.findByToken(token).orElse(null);
        if (onlineToken == null) return null;
        return userService.findByUsername(onlineToken.getUsername());
    }
}
