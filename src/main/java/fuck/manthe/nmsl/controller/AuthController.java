package fuck.manthe.nmsl.controller;

import cn.hutool.crypto.SecureUtil;
import com.standardwebhooks.Webhook;
import com.standardwebhooks.exceptions.WebhookSigningException;
import fuck.manthe.nmsl.entity.*;
import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;
import fuck.manthe.nmsl.entity.webhook.UserInjectMessage;
import fuck.manthe.nmsl.entity.webhook.UserRegisterMessage;
import fuck.manthe.nmsl.entity.webhook.UserRenewMessage;
import fuck.manthe.nmsl.service.*;
import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
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

    @Resource
    QueueService queueService;

    @Resource
    AnalysisService analysisService;

    @Resource
    VapeAccountService vapeAccountService;

    @Resource
    WebhookService webhookService;

    @Value("${share.cold-down.global.enabled}")
    boolean coldDownEnabled;

    @Resource
    private GatewayService gatewayService;

    @PostMapping("/auth.php")
    public String auth(HttpServletRequest request) throws Exception {
        if (gatewayService.isPureGateway()) {
            // This servlet only response for gateway auth requests
            return "It's not you, it's us.";
        }
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
        log.info("User {} tried to inject!", username);
        // 统计启动次数
        analysisService.launchInvoked(username);
        // use gateway
        if (gatewayService.canUseGateway()) {
            Gateway gateway = gatewayService.getOne();
            if (gateway != null) {
                VapeAuthorizeDTO authorize = gatewayService.use(gateway);
                if (authorize != null) {
                    log.info("Successfully authed with the gateway {} ({})", gateway.getName(), gateway.getId());
                    return authorize.getToken();
                }
                log.error("Gateway {} ({}) is not available. Authorizing with this server...", gateway.getName(), gateway.getId());
            }
        }
        // 没有配置gateway或者所有的gateway都在冷却
        // 在这个服务器处理登录
        VapeAccount vapeAccount = vapeAccountService.getOne();
        if (vapeAccount == null) {
            return "No account for you";
        }
        VapeAuthorizeDTO authorize = vapeAccountService.doAuth(vapeAccount);
        // Push to webhook
        UserInjectMessage message = new UserInjectMessage();
        message.setContent("User %s inject".formatted(username));
        message.setUsername(username);
        message.setTimestamp(System.currentTimeMillis() / Webhook.SECOND_IN_MS);
        webhookService.pushAll("inject", message);
        return authorize.getToken();
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

    @PostMapping("redeem")
    public ResponseEntity<RestBean<String>> register(@RequestParam String username, @RequestParam String password, @RequestParam String code) throws WebhookSigningException {
        RedeemCode redeemCode = redeemService.infoOrNull(code);
        if (redeemCode == null)
            return new ResponseEntity<>(RestBean.failure(404, "Code not found."), HttpStatus.NOT_FOUND);
        long expire = -1L;
        if (redeemCode.getDate() != -1) {
            expire = System.currentTimeMillis() + (long) redeemCode.getDate() * 24 * 60 * 60 * 1000;
        }
        if (crackedUserService.addUser(CrackedUser.builder().password(SecureUtil.sha1(password)).username(username).expire(expire).build())) {
            redeemService.useCode(redeemCode.getCode(), username);
            // push to webhooks
            UserRegisterMessage message = new UserRegisterMessage();
            message.setRedeemUsername(username);
            message.setTimestamp(System.currentTimeMillis() / Webhook.SECOND_IN_MS);
            message.setCode(redeemCode.getCode());
            message.setExpireAt(expire);
            message.setContent("用户 %s 使用一个%s天兑换码 %s 注册了账户".formatted(username, redeemCode.getDate(), redeemCode.getCode()));
            webhookService.pushAll("renew", message);

            return ResponseEntity.ok(RestBean.success("Registered."));
        } else if (crackedUserService.isValid(username, password) && crackedUserService.renewUser(username, redeemCode.getDate())) {
            redeemService.useCode(redeemCode.getCode(), username);
            // Push to webhooks
            UserRenewMessage message = new UserRenewMessage();
            message.setRedeemUsername(username);
            message.setTimestamp(System.currentTimeMillis() / Webhook.SECOND_IN_MS);
            message.setCode(redeemCode.getCode());
            message.setExpireAt(crackedUserService.findByUsername(username).getExpire());
            message.setContent("用户 %s 使用%s 兑换了%s 天订阅".formatted(username, redeemCode.getCode(), redeemCode.getDate()));
            webhookService.pushAll("renew", message);
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
