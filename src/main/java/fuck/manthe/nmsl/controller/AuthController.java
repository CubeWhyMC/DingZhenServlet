package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.ColdDown;
import fuck.manthe.nmsl.entity.CrackedUser;
import fuck.manthe.nmsl.entity.RedeemCode;
import fuck.manthe.nmsl.service.impl.CrackedUserServiceImpl;
import fuck.manthe.nmsl.service.impl.RedeemServiceImpl;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

    String adminPassword = System.getProperty("adminPassword", UUID.randomUUID().toString());

    {
        log.warn("Admin password: {}", adminPassword);
    }

    @PostMapping("/auth.php")
    public String auth(HttpServletRequest request) throws Exception {
        String bodyParam = new String(request.getInputStream().readAllBytes());
        Map<String, String> map = decodeParam(bodyParam);
        String email = map.get("email");
        String password = map.get("password");
        log.info("User {} login (PWD HASH {})", email, password.hashCode());
        if (!crackedUserService.isValid(email, password)) {
            return "Unauthorized";
        }
        if (crackedUserService.hasExpired(email)) {
            return "Expired";
        }
        if (Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.COLD_DOWN), 0L) > System.currentTimeMillis()) {
            return "Somebody is injecting";
        }
        log.info("User {} tried to inject!", email);
        try (Response response = httpClient.newCall(new Request.Builder().post(RequestBody.create("email=" + sharedUsername + "&password=" + this.sharedPassword + "&hwid=FUMANTHE&v=v3&t=true", MediaType.parse("application/x-www-form-urlencoded"))).url("https://www.vape.gg/auth.php").header("User-Agent", "Agent_114514").build()).execute()) {
            if (response.body() != null) {
                if (response.isSuccessful()) {
                    redisTemplate.opsForValue().set(Const.COLD_DOWN, System.currentTimeMillis() + 600000);
                }
                return response.body().string();
            }
        }
        log.error("Vape authorize was expired. (wrong password)");
        return "1"; // cert expired
    }

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

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String code, HttpServletResponse response) {
        RedeemCode redeemCode = redeemService.redeem(code);
        if (redeemCode == null) return "Code not found.";
        long expire = -1L;
        if (redeemCode.getDate() != -1) {
            expire = System.currentTimeMillis() + (long) redeemCode.getDate() * 24 * 60 * 60 * 1000;
        }
        if (crackedUserService.addUser(CrackedUser.builder().password(password).username(username).expire(expire).build())) {
            return "Success";
        }
       response.setStatus(HttpServletResponse.SC_CONFLICT);
       return "Account exist";
    }

    @PostMapping("/renew")
    public String renew(@RequestParam String username, @RequestParam String code) {
        RedeemCode redeemCode = redeemService.redeem(code);
        if (redeemCode == null) return "Code not found.";
        if (crackedUserService.renewUser(username, redeemCode.getDate())) {
            return "OK";
        }
        return "Failed";
    }

    @GetMapping("/check")
    public String checkConnection() {
        return "OK";
    }

    @GetMapping("colddown/json")
    public ColdDown coldDownJson() {
        Long next = redisTemplate.opsForValue().get(Const.COLD_DOWN);
        return new ColdDown(next);
    }

    @GetMapping("reset")
    public String reset(@RequestParam String pwd) {
        if (!Objects.equals(pwd, adminPassword)) {
            return "Wrong password";
        }
        redisTemplate.delete(Const.COLD_DOWN);
        return "Reset CD.";
    }

    @GetMapping("user/add")
    public String addUser(@RequestParam(value = "admin") String admin, @RequestParam String username, @RequestParam String password, @RequestParam int day) {
        if (!Objects.equals(adminPassword, admin)) {
            return "Wrong admin password";
        }
        long expire = -1L;
        if (day != -1) {
            expire = System.currentTimeMillis() + (long) day * 24 * 60 * 60 * 1000;
        }
        if (crackedUserService.addUser(CrackedUser.builder().password(password).username(username).expire(expire).build())) {
            return "Success";
        }
        return "Failed";
    }

    @GetMapping("user/renew")
    public String renew(@RequestParam String username, @RequestParam int day) {
        if (crackedUserService.renewUser(username, day)) {
            return "Success";
        }
        return "Failed";
    }

    @GetMapping("user/remove")
    public String removeUser(@RequestParam(value = "admin") String admin, @RequestParam String username) {
        if (!Objects.equals(adminPassword, admin)) {
            return "Wrong admin password";
        }
        crackedUserService.removeUser(username);
        return "Success";
    }
}
