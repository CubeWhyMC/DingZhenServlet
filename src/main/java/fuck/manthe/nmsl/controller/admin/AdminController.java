package fuck.manthe.nmsl.controller.admin;

import cn.hutool.crypto.SecureUtil;
import fuck.manthe.nmsl.entity.CrackedUser;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.dto.AnalysisDTO;
import fuck.manthe.nmsl.entity.dto.CrackedUserDTO;
import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.service.CrackedUserService;
import fuck.manthe.nmsl.service.RedeemService;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin")
@RestController
@Log4j2
public class AdminController {
    @Resource
    RedisTemplate<String, Long> redisTemplate;

    @Resource
    CrackedUserService crackedUserService;

    @Resource
    RedeemService redeemService;

    @Resource
    AnalysisService analysisService;

    @Value("${share.user.auto-delete-expired}")
    boolean autoDeleteExpired;

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

    @GetMapping("analysis")
    public AnalysisDTO analysis() {
        return AnalysisDTO.builder()
                .todayLaunch(analysisService.getTodayLaunch())
                .totalLaunch(analysisService.getTotalLaunch())
                .todayRegister(analysisService.getTodayRegister())
                .currentUsers(crackedUserService.count())
                .build();
    }

    @GetMapping("listUsers")
    public List<CrackedUserDTO> listUsers() {
        return crackedUserService.list().stream().map((user) -> CrackedUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .expire(user.getExpire())
                .totalLaunch(analysisService.getTotalLaunch(user.getUsername()))
                .lastLaunch(analysisService.getLastLaunch(user.getUsername()))
                .build()).toList();
    }

    @GetMapping("logSuper")
    public String logSuper() {
        // code is in Filter
        return "Logged super-admin password in console.";
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void autoJob() {
        if (autoDeleteExpired) {
            crackedUserService.removeExpired();
        }
        analysisService.reset();
    }

    @DeleteMapping("removeExpired")
    @Transactional
    public RestBean<String> removeExpired() {
        crackedUserService.removeExpired();
        return RestBean.success("Success");
    }
}
