package fuck.manthe.nmsl.controller.admin;

import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.util.Const;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("admin/colddown")
public class ColdDownAdminController {
    @Resource
    RedisTemplate<String, Long> redisTemplate;

    @PostMapping("reset")
    public ResponseEntity<RestBean<String>> reset() {
        log.info("Global inject cold down was reset.");
        // todo migrate to VapeAccountService
        redisTemplate.opsForValue().set(Const.COLD_DOWN, System.currentTimeMillis());
        return ResponseEntity.ok(RestBean.success("Reset CD."));
    }
}
