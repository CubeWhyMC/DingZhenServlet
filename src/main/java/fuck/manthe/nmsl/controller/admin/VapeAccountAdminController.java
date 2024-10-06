package fuck.manthe.nmsl.controller.admin;

import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.VapeAccount;
import fuck.manthe.nmsl.entity.dto.*;
import fuck.manthe.nmsl.entity.vo.VapeAccountVO;
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
    public ResponseEntity<RestBean<String>> remove(@RequestBody RemoveSharedAccountDTO dto) {
        if (vapeAccountService.removeAccount(dto.getUsername())) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(RestBean.failure(404, "Account not Found"), HttpStatus.NOT_FOUND);
    }

    @PutMapping("updatePassword")
    public ResponseEntity<RestBean<String>> updatePassword(@RequestBody UpdateSharedPasswordDTO dto) throws Exception {
        if (vapeAccountService.updatePassword(dto.getUsername(), dto.getPassword())) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(RestBean.failure(404, "Account not Found"), HttpStatus.NOT_FOUND);
    }

    @PutMapping("updateHwid")
    public ResponseEntity<RestBean<String>> updateHwid(@RequestBody UpdateHwidDTO dto) throws Exception {
        if (vapeAccountService.updateHwid(dto.getUsername(), dto.getHwid())) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(RestBean.failure(404, "Account not Found"), HttpStatus.NOT_FOUND);
    }

    @PostMapping("resetColddown")
    public RestBean<String> resetColdDown(@RequestBody ResetSharedColdDownDTO dto) {
        vapeAccountService.resetColdDown(vapeAccountService.findByUsername(dto.getUsername()));
        return RestBean.success("Success");
    }
}
