package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.service.QueueService;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class QueueServiceImpl implements QueueService {
    @Resource
    RedisTemplate<String, String> redisTemplate;
    
    @Override
    public List<String> query() {
        return redisTemplate.opsForList().range(Const.QUEUE, 0, -1);
    }

    @Override
    public void quit(String username) {
        redisTemplate.opsForList().remove(Const.QUEUE, 0, username);
    }

    @Override
    public boolean join(String username) {
        if (Objects.requireNonNullElse(redisTemplate.opsForList().range(Const.QUEUE, 0, -1), List.of()).contains(username)) {
            return false;
        }
        redisTemplate.opsForList().leftPush(Const.QUEUE, username);
        return true;
    }

    @Override
    public boolean isInQueue(String username) {
        return Objects.requireNonNull(redisTemplate.opsForList().range(Const.QUEUE, 0, -1)).contains(username);
    }

    @Override
    public boolean isNext(String username) {
        return Objects.requireNonNull(redisTemplate.opsForList().range(Const.QUEUE, -1, -1)).equals(username);
    }
}
