package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.CrackedUser;
import fuck.manthe.nmsl.service.impl.CrackedUserServiceImpl;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@Log4j2
public class AuthController {
    @Resource
    RedisTemplate<String, Long> redisTemplate;
    @Resource
    CrackedUserServiceImpl crackedUserService;

    @Autowired
    OkHttpClient httpClient;

    String username = System.getProperty("username");
    String password = System.getProperty("password");

    String adminPassword = System.getProperty("adminPassword", UUID.randomUUID().toString());

    {
        log.warn("Admin password: {}", adminPassword);
    }

    @RequestMapping(value = "/auth.php", method = {RequestMethod.GET, RequestMethod.POST})
    public String auth(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password) throws Exception {
        if (!crackedUserService.isValid(email, password)) {
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

    @GetMapping("reset")
    public String reset(@RequestParam String pwd) {
        if (!Objects.equals(pwd, adminPassword)) {
            return "Wrong password";
        }
        redisTemplate.delete(Const.COLD_DOWN);
        return "Reset CD.";
    }

    @GetMapping("user/add")
    public String addUser(@RequestParam(value = "admin") String admin, @RequestParam String username, @RequestParam String password) {
        if (!Objects.equals(adminPassword, admin)) {
            return "Wrong admin password";
        }
        if (crackedUserService.addUser(CrackedUser.builder()
                .password(password)
                .username(username)
                .build())) {
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
