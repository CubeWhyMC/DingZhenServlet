package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.CrackedUser;
import fuck.manthe.nmsl.entity.RedeemCode;
import fuck.manthe.nmsl.service.impl.CrackedUserServiceImpl;
import fuck.manthe.nmsl.service.impl.RedeemServiceImpl;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping("/admin")
@RestController
public class AdminController {
    @Resource
    RedisTemplate<String, Long> redisTemplate;
    @Resource
    CrackedUserServiceImpl crackedUserService;
    @Resource
    RedeemServiceImpl redeemService;

    @GetMapping
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Pong", HttpStatus.OK);
    }

    @PostMapping("addUser")
    public ResponseEntity<String> addUser(@RequestParam String username, @RequestParam String password, @RequestParam int day) {
        long expire = -1L;
        if (day != -1) {
            expire = System.currentTimeMillis() + (long) day * 24 * 60 * 60 * 1000;
        }
        if (crackedUserService.addUser(CrackedUser.builder().password(password).username(username).expire(expire).build())) {
            return ResponseEntity.ok("Success");
        }
        return new ResponseEntity<>("Fail", HttpStatus.CONFLICT);
    }

    @PostMapping("colddown/reset")
    public ResponseEntity<String> reset() {
        redisTemplate.opsForValue().set(Const.COLD_DOWN, System.currentTimeMillis());
        return ResponseEntity.ok("Reset CD.");
    }

    @PostMapping("redeem/gen")
    public ResponseEntity<List<RedeemCode>> generateRedeemCode(@RequestParam int count, @RequestParam int day) {
        List<RedeemCode> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            RedeemCode code = generateOne(day);
            redeemService.addCode(code);
            result.add(code);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("renew")
    public ResponseEntity<String> renew(@RequestParam String username, @RequestParam int day) {
        if (crackedUserService.renewUser(username, day)) {
            return ResponseEntity.ok("OK");
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("removeUser")
    public String removeUser(@RequestParam String username) {
        crackedUserService.removeUser(username);
        return "Success";
    }

    @NotNull
    private RedeemCode generateOne(int day) {
        RedeemCode redeemCode = new RedeemCode();
        redeemCode.setDate(day);
        redeemCode.setCode(UUID.randomUUID().toString());
        return redeemCode;
    }
}
