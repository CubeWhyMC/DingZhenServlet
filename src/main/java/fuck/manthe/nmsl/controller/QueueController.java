package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.dto.LoginDTO;
import fuck.manthe.nmsl.service.QueueService;
import fuck.manthe.nmsl.service.UserService;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("colddown/queue")
public class QueueController {
    @Resource
    UserService userService;

    @Resource
    QueueService queueService;

    @PostMapping("join")
    public @NotNull ResponseEntity<RestBean<String>> join(@RequestBody LoginDTO login) {
        if (!userService.isValid(login.getUsername(), login.getPassword()))
            return new ResponseEntity<>(RestBean.unauthorized("Unauthorized"), HttpStatus.UNAUTHORIZED);
        if (!queueService.join(login.getUsername()))
            return new ResponseEntity<>(RestBean.failure(409, "You're always queued."), HttpStatus.CONFLICT);

        return ResponseEntity.ok(RestBean.success("Added " + login.getUsername() + " to queue."));
    }

    @GetMapping("query")
    public List<String> query() {
        return queueService.query();
    }

    @DeleteMapping("quit")
    public @NotNull ResponseEntity<RestBean<String>> quit(@RequestBody LoginDTO login) {
        // 不想玩了可以直接取消名额.
        if (!userService.isValid(login.getUsername(), login.getPassword()))
            return new ResponseEntity<>(RestBean.failure(403, "Unauthorized"), HttpStatus.UNAUTHORIZED);
        queueService.quit(login.getUsername());
        return ResponseEntity.ok(RestBean.success("Removed " + login.getUsername() + " from queue."));
    }
}
