package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.dto.ColdDownDTO;
import fuck.manthe.nmsl.service.VapeAccountService;
import fuck.manthe.nmsl.util.Const;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController()
@RequestMapping("/colddown")
public class ColdDownController {
    @Resource
    RedisTemplate<String, Long> redisTemplate;
    @Resource
    VapeAccountService vapeAccountService;

    @Value("${share.cold-down.global.enabled}")
    boolean globalColdDownEnabled;

    @GetMapping("json")
    public ColdDownDTO coldDownJson() {
        Long next = vapeAccountService.nextAvailable();
        if (globalColdDownEnabled) {
            long globalColdDown = Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.COLD_DOWN), 0L);
            if (next < globalColdDown) {
                next = globalColdDown;
            }
        }
        return new ColdDownDTO(next);
    }
}
