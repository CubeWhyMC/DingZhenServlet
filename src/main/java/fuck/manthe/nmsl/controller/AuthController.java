package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import okhttp3.*;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Objects;

@RestController
public class AuthController {
    @Autowired
    OkHttpClient httpClient;

    String username = System.getProperty("username");
    String password = System.getProperty("password");

    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int STRING_LENGTH = 33;


    @RequestMapping(value = "/auth.php", method = {RequestMethod.GET, RequestMethod.POST})
    public String auth() throws Exception {
        if (username == null || password == null) {
            return generateRandomString(STRING_LENGTH);
        }
        try (Response response = httpClient.newCall(new Request.Builder()
                .post(RequestBody.create("email=" + username + "&password=" + password + "&hwid=FUMANTHE&v=v3&t=true", MediaType.parse("application/x-www-form-urlencoded")))
                .url("https://www.vape.gg/auth.php")
                .header("User-Agent", "Agent_114514")
                .build()).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }
        }
        return "1"; // cert expired
    }

//    @Scheduled(cron = "0 0 0 1/1 * ? ")
//    public void updateToken() {
//        // https://www.vape.gg/auth.php
//
//    }


    public static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALLOWED_CHARACTERS.length());
            sb.append(ALLOWED_CHARACTERS.charAt(index));
        }

        return sb.toString();
    }
}
