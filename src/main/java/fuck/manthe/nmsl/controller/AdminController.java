package fuck.manthe.nmsl.controller;

import cn.hutool.crypto.SecureUtil;
import fuck.manthe.nmsl.entity.Analysis;
import fuck.manthe.nmsl.entity.CrackedUser;
import fuck.manthe.nmsl.entity.RedeemCode;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.repository.RedeemRepository;
import fuck.manthe.nmsl.repository.UserRepository;
import fuck.manthe.nmsl.service.impl.CrackedUserServiceImpl;
import fuck.manthe.nmsl.service.impl.RedeemServiceImpl;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping("/admin")
@RestController
@Log4j2
public class AdminController {
    @Resource
    RedisTemplate<String, Long> redisTemplate;

    @Resource
    CrackedUserServiceImpl crackedUserService;
    @Resource
    RedeemServiceImpl redeemService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedeemRepository redeemRepository;

    @RequestMapping("ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Pong");
    }

    @PostMapping("addUser")
    public ResponseEntity<RestBean<String>> addUser(@RequestParam String username, @RequestParam String password, @RequestParam int day) {
        long expire = -1L;
        log.info("User {} has added by an admin", username);
        if (day != -1) {
            expire = System.currentTimeMillis() + (long) day * 24 * 60 * 60 * 1000;
        }
        if (crackedUserService.addUser(CrackedUser.builder().password(SecureUtil.sha1(password)).username(username).expire(expire).build())) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(RestBean.failure(409, "Conflict"), HttpStatus.CONFLICT);
    }

    @PostMapping("colddown/reset")
    public ResponseEntity<RestBean<String>> reset() {
        log.info("Inject cold down was reset.");
        redisTemplate.opsForValue().set(Const.COLD_DOWN, System.currentTimeMillis());
        return ResponseEntity.ok(RestBean.success("Reset CD."));
    }

    @PostMapping("redeem/gen")
    public ResponseEntity<RestBean<List<RedeemCode>>> generateRedeemCode(@RequestParam int count, @RequestParam int day) {
        List<RedeemCode> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            RedeemCode code = generateOne(day);
            redeemService.addCode(code);
            result.add(code);
        }
        return ResponseEntity.ok(RestBean.success(result));
    }

    @GetMapping("redeem/list")
    public ResponseEntity<RestBean<List<RedeemCode>>> redeemCodeList() {
        return ResponseEntity.ok(RestBean.success(redeemRepository.findAll()));
    }

    @DeleteMapping("redeem/destroy")
    public ResponseEntity<RestBean<String>> destroyRedeemCode(@RequestParam String code) {
        if (redeemService.removeCode(code)) {
            return ResponseEntity.ok(RestBean.success("Success"));
        }
        return new ResponseEntity<>(RestBean.failure(404, "Code not found"), HttpStatus.NOT_FOUND);
    }

    @PostMapping("renew/{username}")
    public ResponseEntity<RestBean<String>> renew(@PathVariable String username, @RequestParam int day) {
        log.info("An admin renewed the expire date of user {} ({}d)", username, day);
        if (crackedUserService.renewUser(username, day)) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("removeUser/{username}")
    public RestBean<Object> removeUser(@PathVariable String username) {
        log.info("An admin removed a user with name {}", username);
        crackedUserService.removeUser(username);
        return RestBean.success();
    }

    @PostMapping("password/{username}/reset")
    public ResponseEntity<RestBean<String>> resetPassword(@PathVariable String username, @RequestParam String password) {
        log.info("An admin reset the password of user {}", username);
        if (crackedUserService.resetPassword(username, password)) {
            return ResponseEntity.ok(RestBean.success());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

//    @GetMapping("restartServlet")
//    public RestBean<String> restartServlet() {
//        log.info("Restart servlet. (Requested by an admin)");
//        restartEndpoint.restart();
//        return RestBean.success();
//    }

    @GetMapping("analysis")
    public Analysis analysis() {
        Long todayLaunch = redisTemplate.opsForValue().get(Const.TODAY_LAUNCH);
        Long totalLaunch = redisTemplate.opsForValue().get(Const.TOTAL_LAUNCH);
        if (totalLaunch == null) totalLaunch = 0L;
        if (todayLaunch == null) todayLaunch = 0L;
        return Analysis.builder()
                .todayLaunch(todayLaunch)
                .totalLaunch(totalLaunch)
                .currentUsers(userRepository.count())
                .build();
    }

    @GetMapping("listUsers")
    public List<CrackedUser> listUsers() {
        return userRepository.findAll();
    }

    @NotNull
    private RedeemCode generateOne(int day) {
        RedeemCode redeemCode = new RedeemCode();
        redeemCode.setDate(day);
        redeemCode.setCode(UUID.randomUUID().toString());
        return redeemCode;
    }
}
