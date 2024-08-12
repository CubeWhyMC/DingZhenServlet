package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    @Resource
    RedisTemplate<String, Integer> redisTemplate;

    @PostConstruct
    public void init() {
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Integer.class));
    }

    @Override
    public void launchInvoked(String username) {
        // global
        redisTemplate.opsForValue().increment(Const.TODAY_LAUNCH);
        redisTemplate.opsForValue().increment(Const.TOTAL_LAUNCH);
        // pre user
        redisTemplate.opsForValue().increment(Const.TOTAL_LAUNCH_PRE_USER + username);
    }

    @Override
    public void authRequested(String username) {
        redisTemplate.opsForValue().increment(Const.TOTAL_REQUEST_AUTH_PRE_USER + username);
    }

    @Override
    public void userRegistered() {
        redisTemplate.opsForValue().increment(Const.TODAY_REGISTER_USER);
    }

    @Override
    public Integer getTodayLaunch() {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.TODAY_LAUNCH), 0);
    }

    @Override
    public Integer getTotalLaunch() {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.TOTAL_LAUNCH), 0);
    }

    @Override
    public Integer getTotalLaunch(String username) {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.TOTAL_LAUNCH_PRE_USER + username), 0);
    }

    @Override
    public Integer getTodayRegister() {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.TODAY_REGISTER_USER), 0);
    }
}
