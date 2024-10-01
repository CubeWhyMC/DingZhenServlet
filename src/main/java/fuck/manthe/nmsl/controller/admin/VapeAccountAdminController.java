package fuck.manthe.nmsl.controller.admin;

import com.standardwebhooks.exceptions.WebhookSigningException;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.VapeAccount;
import fuck.manthe.nmsl.entity.dto.PauseInjectDTO;
import fuck.manthe.nmsl.entity.dto.VapeAccountDTO;
import fuck.manthe.nmsl.entity.vo.VapeAccountVO;
import fuck.manthe.nmsl.entity.webhook.PauseInjectMessage;
import fuck.manthe.nmsl.service.VapeAccountService;
import fuck.manthe.nmsl.service.WebhookService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/admin/shared")
public class VapeAccountAdminController {
    @Resource
    VapeAccountService vapeAccountService;

    @Resource
    WebhookService webhookService;

    @GetMapping("list")
    public RestBean<List<VapeAccountVO>> list() {
        return RestBean.success(vapeAccountService.listAccounts().stream().map(account -> VapeAccountVO.builder()
                .hwid(account.getHwid())
                .username(account.getUsername())
                .colddown(vapeAccountService.getColdDown(account))
                .build()).toList());
    }

    @PostMapping("add")
    public ResponseEntity<RestBean<String>> add(@RequestBody VapeAccountDTO vapeAccount) throws Exception {
        if (vapeAccountService.addAccount(VapeAccount.builder()
                .username(vapeAccount.getUsername())
                .password(vapeAccount.getPassword())
                .hwid(vapeAccount.getHwid()).build())) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(RestBean.failure(409, "Conflict"), HttpStatus.CONFLICT);
    }

    @DeleteMapping("remove")
    public ResponseEntity<RestBean<String>> remove(@RequestParam String username) {
        if (vapeAccountService.removeAccount(username)) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(RestBean.failure(404, "Account not Found"), HttpStatus.NOT_FOUND);
    }

    @PutMapping("updatePassword")
    public ResponseEntity<RestBean<String>> updatePassword(@RequestParam String username, @RequestParam String newPassword) throws Exception {
        if (vapeAccountService.updatePassword(username, newPassword)) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(RestBean.failure(404, "Account not Found"), HttpStatus.NOT_FOUND);
    }

    @PutMapping("updateHwid")
    public ResponseEntity<RestBean<String>> updateHwid(@RequestParam String username, @RequestParam String newHwid) throws Exception {
        if (vapeAccountService.updateHwid(username, newHwid)) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(RestBean.failure(404, "Account not Found"), HttpStatus.NOT_FOUND);
    }

    @PostMapping("resetColddown")
    public RestBean<String> resetColdDown(@RequestParam String username) {
        vapeAccountService.resetColdDown(vapeAccountService.findByUsername(username));
        return RestBean.success("Success");
    }

    @PostMapping("pauseInject")
    public RestBean<String> pauseLogin(@RequestBody PauseInjectDTO dto) throws WebhookSigningException {
        PauseInjectMessage message = new PauseInjectMessage(dto.isInjectEnabled());
        message.setTimestamp(System.currentTimeMillis() / 1000L);
        message.setState(dto.isInjectEnabled());

        vapeAccountService.pauseInject(dto.isInjectEnabled());
        if (!dto.isInjectEnabled()) {
            message.setContent("已暂停注入,现在只有永久用户可以注入");
            log.info("Injecting vape was limited to lifetime users only.");
        } else {
            message.setContent("已允许注入,现在所有人都可以注入");
            log.info("Unlimited injecting.");
        }
        webhookService.pushAll("pause-inject", message); // push to webhooks

        return RestBean.success("Success");
    }
}
