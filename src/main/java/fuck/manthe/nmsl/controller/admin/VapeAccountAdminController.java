package fuck.manthe.nmsl.controller.admin;

import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.VapeAccount;
import fuck.manthe.nmsl.entity.dto.VapeAccountDTO;
import fuck.manthe.nmsl.entity.vo.VapeAccountVO;
import fuck.manthe.nmsl.service.VapeAccountService;
import fuck.manthe.nmsl.utils.CryptUtil;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/shared")
public class VapeAccountAdminController {
    @Resource
    VapeAccountService vapeAccountService;
    @Resource
    CryptUtil cryptUtil;

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
                .password(cryptUtil.encryptString(vapeAccount.getPassword()))
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
}
