package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import okhttp3.*;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Objects;

@RestController
public class AuthController {
    @Resource
    RedisTemplate<String, Long> redisTemplate;

    @Autowired
    OkHttpClient httpClient;

    String username = System.getProperty("username");
    String password = System.getProperty("password");

    String secretUsername = System.getProperty("secretUsername");
    String resetPassword = System.getProperty("resetPassword");

    @RequestMapping(value = "/auth.php", method = {RequestMethod.GET, RequestMethod.POST})
    public String auth(@RequestParam(value = "email") String email) throws Exception {
        if (secretUsername != null && !secretUsername.equals(email)) {
            return "Unauthorized";
        }
        if (Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.COLD_DOWN), 0L) > System.currentTimeMillis()) {
            return "Somebody is injecting";
        }
        try (Response response = httpClient.newCall(new Request.Builder()
                .post(RequestBody.create("email=" + username + "&password=" + password + "&hwid=FUMANTHE&v=v3&t=true", MediaType.parse("application/x-www-form-urlencoded")))
                .url("https://www.vape.gg/auth.php")
                .header("User-Agent", "Agent_114514")
                .build()).execute()) {
            if (response.body() != null) {
                redisTemplate.opsForValue().set(Const.COLD_DOWN, System.currentTimeMillis() + 600000);
                return response.body().string();
            }
        }
        return "1"; // cert expired
    }

    @RequestMapping("reset")
    public String reset(@RequestParam String pwd) {
        if (!Objects.equals(pwd, resetPassword)) {
            return "Wrong password";
        }
        redisTemplate.delete(Const.COLD_DOWN);
        return "Reset CD.";
    }

//    @Scheduled(cron = "0 0 0 1/1 * ? ")
//    public void updateToken() {
//        // https://www.vape.gg/auth.php
//
//    }
}
