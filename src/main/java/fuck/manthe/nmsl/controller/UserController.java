package fuck.manthe.nmsl.controller;

import com.standardwebhooks.exceptions.WebhookSigningException;
import fuck.manthe.nmsl.entity.RedeemCode;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.User;
import fuck.manthe.nmsl.entity.dto.ForgetPasswordDTO;
import fuck.manthe.nmsl.entity.dto.RedeemDTO;
import fuck.manthe.nmsl.entity.webhook.UserRegisterMessage;
import fuck.manthe.nmsl.entity.webhook.UserRenewMessage;
import fuck.manthe.nmsl.service.RedeemService;
import fuck.manthe.nmsl.service.UserService;
import fuck.manthe.nmsl.service.WebhookService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;

    @Resource
    RedeemService redeemService;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    WebhookService webhookService;

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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RestBean.failure(400, "Code was redeemed"));
        }

        String username = dto.getUsername();
        String password = dto.getPassword();
        User user = userService.register(username, password, expire);
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
        return new ResponseEntity<>(RestBean.failure(409, "用户存在或者密码输入错误"), HttpStatus.CONFLICT);
    }

    @PostMapping("forgetPassword")
    public ResponseEntity<RestBean<String>> forgetPassword(@RequestBody ForgetPasswordDTO dto) {
        User user = userService.findByUsername(dto.getUsername());
        if (user == null) {
            return new ResponseEntity<>(RestBean.failure(404, "用户不存在"), HttpStatus.NOT_FOUND);
        }

        RedeemCode redeemCode = redeemService.infoOrNull(dto.getRedeemCode());
        if (redeemCode == null || redeemCode.isAvailable() || redeemCode.getRedeemer() == null || !redeemCode.getRedeemer().getId().equals(user.getId())) {
            // 邀请码找不到或者根本没被人用过
            // 写到一起是为了防止被刷API
            return new ResponseEntity<>(RestBean.failure(404, "邀请码不存在"), HttpStatus.NOT_FOUND);
        }
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userService.save(user);
        return ResponseEntity.ok(RestBean.success("成功重置密码"));
    }
}
