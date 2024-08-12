package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    @Resource
    RedisTemplate<String, Long> redisTemplate;

    @Override
    public void launchInvoked(String username) {
        // global
        redisTemplate.opsForValue().increment(Const.TODAY_LAUNCH, 1L);
        redisTemplate.opsForValue().increment(Const.TOTAL_LAUNCH, 1L);
        // pre user
        redisTemplate.opsForValue().increment(Const.TOTAL_LAUNCH_PRE_USER + username, 1L);
    }

    @Override
    public void authRequested(String username) {
        redisTemplate.opsForValue().increment(Const.TOTAL_REQUEST_AUTH_PRE_USER + username, 1L);
    }

    @Override
    public void userRegistered() {
        redisTemplate.opsForValue().increment(Const.TODAY_REGISTER_USER, 1L);
    }
}
