package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.service.impl.CrackedUserServiceImpl;
import fuck.manthe.nmsl.service.impl.QueueServiceImpl;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("colddown/queue")
public class QueueController {
    @Resource
    RedisTemplate<String, String> redisTemplate;
    @Resource
    CrackedUserServiceImpl crackedUserService;
    @Resource
    QueueServiceImpl queueService;

    @PostMapping("join")
    public @NotNull ResponseEntity<RestBean<String>> join(@RequestParam String username, @RequestParam String password) {
        if (!crackedUserService.isValid(username, password)) return new ResponseEntity<>(RestBean.unauthorized("Unauthorized"), HttpStatus.UNAUTHORIZED);
        if (!queueService.join(username)) {
            return new ResponseEntity<>(RestBean.failure(409, "You're always queued."), HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(RestBean.success("Added " + username + " to queue."));
    }

    @GetMapping("query")
    public List<String> query() {
        return queueService.query();
    }

    @DeleteMapping("remove")
    public @NotNull RestBean<String> remove(@RequestParam String username, @RequestParam String password) {
        // 不想玩了可以直接取消名额.
        queueService.quit(username);
        return RestBean.success("Removed " + username + " from queue.");
    }
}
