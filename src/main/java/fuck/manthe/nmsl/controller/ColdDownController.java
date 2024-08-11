package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.ColdDown;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/colddown")
public class ColdDownController {
    @Resource
    RedisTemplate<String, Long> redisTemplate;


    @GetMapping("colddown/json")
    public ColdDown coldDownJson() {
        Long next = redisTemplate.opsForValue().get(Const.COLD_DOWN);
        return new ColdDown(next);
    }
}
