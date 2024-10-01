package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    @Resource(shareable = false)
    RedisTemplate<String, Long> redisTemplate;

    @Override
    public void launchInvoked(String username) {
        // global
        redisTemplate.opsForValue().increment(Const.TODAY_LAUNCH);
        redisTemplate.opsForValue().increment(Const.TOTAL_LAUNCH);
        // pre user
        redisTemplate.opsForValue().increment(Const.TOTAL_LAUNCH_PRE_USER + username);
        redisTemplate.opsForValue().set(Const.LAST_INJECT_TIME + username, System.currentTimeMillis());
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
    public long getTodayLaunch() {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.TODAY_LAUNCH), 0L);
    }

    @Override
    public long getTotalLaunch() {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.TOTAL_LAUNCH), 0L);
    }

    @Override
    public long getTotalLaunch(String username) {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.TOTAL_LAUNCH_PRE_USER + username), 0L);
    }

    @Override
    public long getTodayRegister() {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.TODAY_REGISTER_USER), 0L);
    }

    @Override
    public void reset() {
        redisTemplate.opsForValue().set(Const.TODAY_LAUNCH, 0L);
        redisTemplate.opsForValue().set(Const.TODAY_REGISTER_USER, 0L);
    }

    @Override
    public long getLastLaunch(String username) {
        return Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.LAST_INJECT_TIME + username), -1L);
    }
}
