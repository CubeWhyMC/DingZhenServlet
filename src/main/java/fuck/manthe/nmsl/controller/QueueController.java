package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.service.impl.CrackedUserServiceImpl;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController("colddown/queue")
public class QueueController {
    @Resource
    RedisTemplate<String, String> redisTemplate;
    @Resource
    CrackedUserServiceImpl crackedUserService;

    @PostMapping("join")
    public @NotNull ResponseEntity<RestBean<String>> join(@RequestParam String username, @RequestParam String password) {
        if (!crackedUserService.isValid(username, password)) return new ResponseEntity<>(RestBean.unauthorized("Unauthorized"), HttpStatus.UNAUTHORIZED);
        if (Objects.requireNonNullElse(redisTemplate.opsForList().range(Const.QUEUE, 0, -1), List.of()).contains(username)) {
            return new ResponseEntity<>(RestBean.failure(409, "You're always queued."), HttpStatus.CONFLICT);
        }
        redisTemplate.opsForList().leftPush(Const.QUEUE, username);
        return ResponseEntity.ok(RestBean.success("Added " + username + " to queue."));
    }

    @GetMapping("query")
    public List<String> query() {
        return redisTemplate.opsForList().range(Const.QUEUE, 0, -1);
    }

    @DeleteMapping("remove")
    public @NotNull RestBean<String> remove(@RequestParam String username, @RequestParam String password) {
        // 不想玩了可以直接取消名额.
        redisTemplate.opsForList().remove(Const.QUEUE, 0, username);
        return RestBean.success("Removed " + username + " from queue.");
    }
}
