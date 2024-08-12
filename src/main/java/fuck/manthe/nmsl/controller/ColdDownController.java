package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.ColdDown;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("colddown")
public class ColdDownController {
    @Resource
    RedisTemplate<String, Long> redisTemplate;

    @Value("${share.cold-down.global.enabled}")
    boolean globalColdDownEnabled;

    @GetMapping("json")
    public ColdDown coldDownJson() {
        if (!globalColdDownEnabled) return new ColdDown(-1L);
        Long next = redisTemplate.opsForValue().get(Const.COLD_DOWN);
        return new ColdDown(next);
    }
}
