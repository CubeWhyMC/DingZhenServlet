package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.service.MaintenanceService;
import fuck.manthe.nmsl.service.UserService;
import fuck.manthe.nmsl.util.Const;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Log4j2
@Service
public class MaintenanceServiceImpl implements MaintenanceService {
    @Resource
    RedisTemplate<String, Boolean> booleanRedisTemplate;

    @Resource
    RedisTemplate<String, Long> longRedisTemplate;

    @Resource
    UserService userService;

    @Value("${share.maintain.auto-renew}")
    boolean shouldRenew;

    @Override
    public boolean isMaintaining() {
        return Boolean.TRUE.equals(booleanRedisTemplate.opsForValue().get(Const.IS_MAINTAINING));
    }

    @Override
    public void setMaintaining(boolean maintaining) {
        if (maintaining) {
            log.info("Maintain mode enabled.");
            booleanRedisTemplate.opsForValue().set(Const.IS_MAINTAINING, true);
            longRedisTemplate.opsForValue().set(Const.MAINTAIN_START_AT, System.currentTimeMillis());
        } else {
            booleanRedisTemplate.opsForValue().set(Const.IS_MAINTAINING, false);
            int days = calculateDuration();
            if (shouldRenew && days > 0) {
                userService.renewAll(days);
            }
            log.info("Maintain mode disabled.");
        }
    }

    @Override
    public long getStartTime() {
        if (!isMaintaining()) {
            return -1L;
        }
        return Objects.requireNonNullElse(longRedisTemplate.opsForValue().get(Const.MAINTAIN_START_AT), -1L);
    }

    @Override
    public int calculateDuration() {
        long startTime = getStartTime();
        if (startTime == -1) {
            return 0;
        }
        long durationInMillis = System.currentTimeMillis() - startTime;
        int durationInDays = (int) (durationInMillis / (1000 * 60 * 60 * 24));
        durationInDays = durationInDays < 1 ? 0 : durationInDays;
        return durationInDays;
    }
}
