package fuck.manthe.nmsl.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import fuck.manthe.nmsl.entity.CrackedUser;
import fuck.manthe.nmsl.entity.RedeemCode;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.VapeAccount;
import fuck.manthe.nmsl.service.*;
import fuck.manthe.nmsl.utils.Const;
import fuck.manthe.nmsl.utils.CryptUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Log4j2
public class AuthController {
    @Resource
    RedisTemplate<String, Long> redisTemplate;
    @Resource
    CrackedUserService crackedUserService;
    @Resource
    RedeemService redeemService;

    @Autowired
    OkHttpClient httpClient;

    @Resource
    QueueService queueService;
    @Resource
    AnalysisService analysisService;
    @Resource
    VapeAccountService vapeAccountService;

    @Value("${share.cold-down.global.enabled}")
    boolean coldDownEnabled;
    @Value("${share.cold-down.global.during-min}")
    int coldDownMin;

    @Value("${share.cold-down.global.during-max}")
    int coldDownMax;
    @Autowired
    private CryptUtil cryptUtil;

    @PostMapping("/auth.php")
    public String auth(HttpServletRequest request) throws Exception {
        String bodyParam = new String(request.getInputStream().readAllBytes());
        Map<String, String> map = decodeParam(bodyParam);
        String username = map.get("email");
        String password = map.get("password");
        log.info("User {} login", username);
        // 统计请求次数
        analysisService.authRequested(username);
        if (!crackedUserService.isValid(username, password)) {
            return "Unauthorized";
        }
        if (crackedUserService.hasExpired(username)) {
            return "Expired";
        }
        if (coldDownEnabled && Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.COLD_DOWN), 0L) > System.currentTimeMillis()) {
            return "Somebody is injecting";
        }
        // 排队机制
        if (queueService.state() && !queueService.isNext(username)) {
            return "Not your turn";
        }
        // Get an account from database
        VapeAccount vapeAccount = vapeAccountService.getOne();
        if (vapeAccount == null) {
            return "No account for you";
        }
        log.info("User {} tried to inject!", username);
        // 统计启动次数
        analysisService.launchInvoked(username);
        try (Response response = httpClient.newCall(new Request.Builder().post(okhttp3.RequestBody.create("email=" + vapeAccount.getUsername() + "&password=" + cryptUtil.decryptStringToString(vapeAccount.getPassword()) + "&hwid=" + vapeAccount.getHwid() + "&v=v3&t=true", MediaType.parse("application/x-www-form-urlencoded"))).url("http://www.vape.gg/auth.php").header("User-Agent", "Agent_" + vapeAccount.getHwid()).build()).execute()) {
            if (response.body() != null) {
                String responseString = response.body().string();
                if (response.isSuccessful()) {
                    if (coldDownEnabled) {
                        redisTemplate.opsForValue().set(Const.COLD_DOWN, System.currentTimeMillis() + (long) RandomUtil.randomInt(coldDownMin, coldDownMax, true, true) * 60 * 1000);
                    }
                    if (responseString.length() != 33) {
                        // not a valid token
                        log.error("Account {} seems banned or incorrect ({})", vapeAccount.getUsername(), responseString);
                    }
                }
                return responseString;
            }
        }
        log.error("Vape authorize was expired. ({}, {})", vapeAccount.getUsername(), vapeAccount.getHwid());
        return "1"; // cert expired
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
    public ResponseEntity<RestBean<String>> register(@RequestParam String username, @RequestParam String password, @RequestParam String code) {
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
