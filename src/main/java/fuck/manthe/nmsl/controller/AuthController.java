package fuck.manthe.nmsl.controller;

import cn.hutool.crypto.SecureUtil;
import fuck.manthe.nmsl.entity.ColdDown;
import fuck.manthe.nmsl.entity.CrackedUser;
import fuck.manthe.nmsl.entity.RedeemCode;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.service.impl.CrackedUserServiceImpl;
import fuck.manthe.nmsl.service.impl.RedeemServiceImpl;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@RestController
@Log4j2
public class AuthController {
    @Resource
    RedisTemplate<String, Long> redisTemplate;
    @Resource
    CrackedUserServiceImpl crackedUserService;
    @Resource
    RedeemServiceImpl redeemService;

    @Autowired
    OkHttpClient httpClient;

    String sharedUsername = System.getProperty("username");
    String sharedPassword = System.getProperty("password");

    @PostMapping("/auth.php")
    public String auth(HttpServletRequest request) throws Exception {
        String bodyParam = new String(request.getInputStream().readAllBytes());
        Map<String, String> map = decodeParam(bodyParam);
        String email = map.get("email");
        String password = map.get("password");
        log.info("User {} login", email);
        if (!crackedUserService.isValid(email, password)) {
            return "Unauthorized";
        }
        if (crackedUserService.hasExpired(email)) {
            return "Expired";
        }
        if (Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.COLD_DOWN), 0L) > System.currentTimeMillis()) {
            return "Somebody is injecting";
        }
        // 排队机制

        log.info("User {} tried to inject!", email);
        Long todayLaunch = redisTemplate.opsForValue().get(Const.TODAY_LAUNCH);
        Long totalLaunch = redisTemplate.opsForValue().get(Const.TOTAL_LAUNCH);
        if (todayLaunch == null) todayLaunch = 0L;
        if (totalLaunch == null) totalLaunch = 0L;
        redisTemplate.opsForValue().set(Const.TODAY_LAUNCH, ++todayLaunch, Duration.ofDays(1));
        redisTemplate.opsForValue().set(Const.TOTAL_LAUNCH, ++totalLaunch);
        try (Response response = httpClient.newCall(new Request.Builder().post(okhttp3.RequestBody.create("email=" + sharedUsername + "&password=" + this.sharedPassword + "&hwid=FUMANTHE&v=v3&t=true", MediaType.parse("application/x-www-form-urlencoded"))).url("https://www.vape.gg/auth.php").header("User-Agent", "Agent_114514").build()).execute()) {
            if (response.body() != null) {
                if (response.isSuccessful()) {
                    redisTemplate.opsForValue().set(Const.COLD_DOWN, System.currentTimeMillis() + (long) randomColdDown() * 60 * 1000);
                }
                return response.body().string();
            }
        }
        log.error("Vape authorize was expired. (wrong password)");
        return "1"; // cert expired
    }

    private int randomColdDown() {
        Random random = new Random();
        int min = 3;
        int max = 6;
        return random.nextInt(max - min + 1) + min;
    }

    @NotNull
    private Map<String, String> decodeParam(String encodedString) {
        String decodedString = URLDecoder.decode(encodedString, StandardCharsets.UTF_8);
        Map<String, String> map = new HashMap<>();
        String[] pairs = decodedString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                map.put(keyValue[0], keyValue[1]);
            }
        }
        return map;
    }

    @PostMapping("/redeem")
    public ResponseEntity<RestBean<String>> register(@RequestParam String username, @RequestParam String password, @RequestParam String code, HttpServletResponse response) {
        RedeemCode redeemCode = redeemService.infoOrNull(code);
        if (redeemCode == null)
            return new ResponseEntity<>(RestBean.failure(404, "Code not found."), HttpStatus.NOT_FOUND);
        long expire = -1L;
        if (redeemCode.getDate() != -1) {
            expire = System.currentTimeMillis() + (long) redeemCode.getDate() * 24 * 60 * 60 * 1000;
        }
        if (crackedUserService.addUser(CrackedUser.builder().password(SecureUtil.sha1(password)).username(username).expire(expire).build())) {
            redeemService.removeCode(redeemCode.getCode());
            return ResponseEntity.ok(RestBean.success("Registered."));
        } else if (crackedUserService.isValid(username, password) && crackedUserService.renewUser(username, redeemCode.getDate())) {
            redeemService.removeCode(redeemCode.getCode());
            return ResponseEntity.ok(RestBean.success("Renewed."));
        }
        return new ResponseEntity<>(RestBean.failure(409, "User exists or wrong password"), HttpStatus.CONFLICT);
    }

    @GetMapping("/check")
    public String checkConnection() {
        return "OK";
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String username, @RequestParam String password) {
        if (!crackedUserService.isValid(username, password) || crackedUserService.hasExpired(username)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok("Valid user");
    }
}
