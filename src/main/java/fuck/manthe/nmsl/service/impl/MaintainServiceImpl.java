package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.service.CrackedUserService;
import fuck.manthe.nmsl.service.MaintainService;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Log4j2
@Service
public class MaintainServiceImpl implements MaintainService {
    @Resource
    RedisTemplate<String, Boolean> booleanRedisTemplate;

    @Resource
    RedisTemplate<String, Long> longRedisTemplate;

    @Resource
    CrackedUserService crackedUserService;

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
            if (shouldRenew) {
                crackedUserService.renewAll(calculateDuration());
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
        return 0;
    }
}
