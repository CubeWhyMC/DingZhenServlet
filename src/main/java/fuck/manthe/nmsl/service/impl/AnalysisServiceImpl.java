package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.util.Const;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    @Resource
    RedisTemplate<String, Integer> redisTemplate;

    @Resource
    RedisTemplate<String, Long> longRedisTemplate;

    @Override
    public void launchInvoked(String username) {
        // global
        redisTemplate.opsForValue().increment(Const.TODAY_LAUNCH);
        redisTemplate.opsForValue().increment(Const.TOTAL_LAUNCH);
        // pre user
        redisTemplate.opsForValue().increment(Const.TOTAL_LAUNCH_PRE_USER + username);
        longRedisTemplate.opsForValue().set(Const.LAST_INJECT_TIME + username, System.currentTimeMillis());
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
    public int getTodayLaunch() {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.TODAY_LAUNCH), 0);
    }

    @Override
    public int getTotalLaunch() {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.TOTAL_LAUNCH), 0);
    }

    @Override
    public int getTotalLaunch(String username) {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.TOTAL_LAUNCH_PRE_USER + username), 0);
    }

    @Override
    public int getTodayRegister() {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.TODAY_REGISTER_USER), 0);
    }

    @Override
    public void reset() {
        redisTemplate.opsForValue().set(Const.TODAY_LAUNCH, 0);
        redisTemplate.opsForValue().set(Const.TODAY_REGISTER_USER, 0);
    }

    @Override
    public long getLastLaunch(String username) {
        return Objects.requireNonNullElse(longRedisTemplate.opsForValue().get(Const.LAST_INJECT_TIME + username), -1L);
    }
}
