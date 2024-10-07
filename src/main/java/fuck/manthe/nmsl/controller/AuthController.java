package fuck.manthe.nmsl.controller;

import com.standardwebhooks.exceptions.WebhookSigningException;
import fuck.manthe.nmsl.entity.*;
import fuck.manthe.nmsl.entity.dto.RedeemDTO;
import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;
import fuck.manthe.nmsl.entity.dto.VerifyLoginDTO;
import fuck.manthe.nmsl.entity.webhook.UserInjectMessage;
import fuck.manthe.nmsl.entity.webhook.UserRegisterMessage;
import fuck.manthe.nmsl.entity.webhook.UserRenewMessage;
import fuck.manthe.nmsl.service.*;
import fuck.manthe.nmsl.util.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    UserService userService;

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

    @Resource
    GatewayService gatewayService;

    @Resource
    MaintenanceService maintenanceService;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    OnlineConfigService onlineConfigService;

    @Value("${share.cold-down.global.enabled}")
    boolean coldDownEnabled;

    @GetMapping("paused")
    public RestBean<Boolean> paused() {
        return RestBean.success(maintenanceService.isMaintaining());
    }

    @PostMapping("auth.php")
    public ResponseEntity<String> auth(HttpServletRequest request) throws Exception {
        // Error message format: ERR CODE) MESSAGE
        if (gatewayService.isPureGateway()) {
            // This servlet only response for gateway auth requests
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(ErrorCode.SERVER.formatError("It's not you, it's us."));
        }
        String bodyParam = new String(request.getInputStream().readAllBytes());
        Map<String, String> map = decodeParam(bodyParam);
        String username = map.get("email");
        String password = map.get("password");
        log.info("User {} login", username);
        User crackedUser = userService.findByUsername(username);
        if (maintenanceService.isMaintaining() && crackedUser.getExpire() != -1) {
            // 暂停注入
            log.info("Blocked user {} to inject. Injections are only open to lifetime users. (Maintaining)", username);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ErrorCode.SERVER.formatError("Maintaining"));
        }
        analysisService.authRequested(username);
        if (!userService.isValid(username, password)) {
            // 凭证错误
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorCode.ACCOUNT.formatError("Unauthorized"));
        }
        if (userService.hasExpired(username)) {
            // 账户失效
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCode.ACCOUNT.formatError("Expired"));
        }
        if (coldDownEnabled && Objects.requireNonNullElse(redisTemplate.opsForValue().get(Const.COLD_DOWN), 0L) > System.currentTimeMillis()) {
            // 公共冷却
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ErrorCode.GLOBAL_COLD_DOWN.formatError("Inject colddown"));
        }
        // 排队机制
        if (queueService.state() && !queueService.isNext(username)) {
            // 排队
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ErrorCode.QUEUE.formatError("Not your turn"));
        }
        // Get an account from database
        log.info("User {} tried to inject!", username);
        // 统计启动次数
        analysisService.launchInvoked(username);
        // auth with vape.gg
        String token = null;
        // use gateway

        if (gatewayService.canUseGateway()) {
            Gateway gateway = gatewayService.getOne();
            if (gateway != null) {
                VapeAuthorizeDTO authorize = gatewayService.use(gateway);
                if (authorize != null) {
                    log.info("Successfully authed with the gateway {} ({})", gateway.getName(), gateway.getId());
                    token = authorize.getToken();
                }
                log.error("Gateway {} ({}) is not available. Authorizing with this server...", gateway.getName(), gateway.getId());
            }
        }
        // 没有配置gateway或者所有的gateway都在冷却
        // 在这个服务器处理登录
        if (token == null) {
            // 上一步失败了或者是没有可用的Gateway
            VapeAccount vapeAccount = vapeAccountService.getOne();
            if (vapeAccount == null) {
                // 每个账户的冷却
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ErrorCode.PRE_ACCOUNT_COLD_DOWN.formatError("Inject cold down"));
            }
            VapeAuthorizeDTO authorize = vapeAccountService.doAuth(vapeAccount);
            if (authorize.getStatus() != VapeAuthorizeDTO.Status.OK) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(authorize.getToken());
            }
            token = authorize.getToken();
        }

        // Push to webhook
        UserInjectMessage message = new UserInjectMessage();
        message.setContent("User %s inject".formatted(username));
        message.setUsername(username);
        message.setTimestamp(System.currentTimeMillis() / 1000L);
        webhookService.pushAll("inject", message);
        // Cache with online config service
        onlineConfigService.cache(token.substring(1), username); // 看起来是UUID
        return ResponseEntity.ok(token);
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
    public ResponseEntity<RestBean<String>> redeem(@RequestBody RedeemDTO dto) throws WebhookSigningException {
        RedeemCode redeemCode = redeemService.infoOrNull(dto.getCode());
        if (redeemCode == null)
            return new ResponseEntity<>(RestBean.failure(404, "Code not found."), HttpStatus.NOT_FOUND);
        long expire = -1L;
        if (redeemCode.getDate() != -1) {
            expire = System.currentTimeMillis() + (long) redeemCode.getDate() * 24 * 60 * 60 * 1000;
        }
        if (!redeemCode.isAvailable()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RestBean.failure(401, "Code was redeemed"));
        }

        String username = dto.getUsername();
        String password = dto.getPassword();
        User user = userService.addUser(User.builder().password(passwordEncoder.encode(password)).username(username).role("USER").expire(expire).build());
        if (user != null) {
            if (redeemService.useCode(redeemCode.getCode(), user)) {
                log.info("User {} registered it's account with the code {} ({}d).", username, redeemCode.getCode(), redeemCode.getDate());
            }
            // push to webhooks
            UserRegisterMessage message = new UserRegisterMessage();
            message.setRedeemUsername(username);
            message.setTimestamp(System.currentTimeMillis() / 1000L);
            message.setCode(redeemCode.getCode());
            message.setExpireAt(expire);
            message.setContent("用户 %s 使用一个%s天兑换码 %s 注册了账户".formatted(username, redeemCode.getDate(), redeemCode.getCode()));
            webhookService.pushAll("renew", message);

            return ResponseEntity.ok(RestBean.success("Registered."));
        } else if (userService.isValid(username, password) && userService.renew(username, redeemCode.getDate())) {
            User existUser = userService.findByUsername(username);
            if (redeemService.useCode(redeemCode.getCode(), existUser)) {
                log.info("User {} renewed it's account with the code {} ({}d).", username, redeemCode.getCode(), redeemCode.getDate());
            }
            // Push to webhooks
            UserRenewMessage message = new UserRenewMessage();
            message.setRedeemUsername(username);
            message.setTimestamp(System.currentTimeMillis() / 1000L);
            message.setCode(redeemCode.getCode());
            message.setExpireAt(userService.findByUsername(username).getExpire());
            message.setContent("用户 %s 使用%s 兑换了%s 天订阅".formatted(username, redeemCode.getCode(), redeemCode.getDate()));
            webhookService.pushAll("renew", message);
            return ResponseEntity.ok(RestBean.success("Renewed."));
        }
        return new ResponseEntity<>(RestBean.failure(409, "User exists or wrong password"), HttpStatus.CONFLICT);
    }

    @GetMapping("check")
    public String checkConnection() {
        return "OK";
    }

    @Deprecated
    @GetMapping("verify")
    public ResponseEntity<String> verify(@RequestParam String username, @RequestParam String password) {
        if (!userService.isValid(username, password) || userService.hasExpired(username)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok("Valid user");
    }

    @PostMapping("verify")
    public ResponseEntity<String> verify(@RequestBody VerifyLoginDTO dto) {
        if (!userService.isValid(dto.getUsername(), dto.getPassword()) || userService.hasExpired(dto.getUsername())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok("Valid user");
    }
}
