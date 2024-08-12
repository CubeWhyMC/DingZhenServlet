package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.VapeAccount;
import fuck.manthe.nmsl.entity.dto.VapeAccountDTO;
import fuck.manthe.nmsl.service.impl.VapeAccountServiceImpl;
import fuck.manthe.nmsl.utils.CryptUtil;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/shared")
public class VapeAccountAdminController {
    @Resource
    VapeAccountServiceImpl vapeAccountService;
    @Resource
    CryptUtil cryptUtil;

    @GetMapping("list")
    public RestBean<List<VapeAccountDTO>> list() {
        List<VapeAccount> accounts = vapeAccountService.listAccounts();
        List<VapeAccountDTO> result = new ArrayList<>();
        for (VapeAccount account : accounts) {
            result.add(VapeAccountDTO.builder()
                    .username(account.getUsername())
                    .hwid(account.getHwid())
                    .build());
        }
        return RestBean.success(result);
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
}
