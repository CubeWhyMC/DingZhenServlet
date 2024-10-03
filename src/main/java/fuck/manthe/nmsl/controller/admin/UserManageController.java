package fuck.manthe.nmsl.controller.admin;

import cn.hutool.crypto.SecureUtil;
import fuck.manthe.nmsl.entity.CrackedUser;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.dto.AddUserDTO;
import fuck.manthe.nmsl.entity.dto.CrackedUserDTO;
import fuck.manthe.nmsl.entity.dto.RenewDTO;
import fuck.manthe.nmsl.entity.dto.ResetPasswordDTO;
import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.service.CrackedUserService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/admin/user")
public class UserManageController {
    @Resource
    CrackedUserService crackedUserService;

    @Resource
    AnalysisService analysisService;

    @GetMapping("list")
    public List<CrackedUserDTO> listUsers() {
        return crackedUserService.list().stream().map((user) -> CrackedUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .expire(user.getExpire())
                .totalLaunch(analysisService.getTotalLaunch(user.getUsername()))
                .lastLaunch(analysisService.getLastLaunch(user.getUsername()))
                .build()).toList();
    }

    @PostMapping("add")
    public ResponseEntity<RestBean<String>> addUser(@RequestBody AddUserDTO dto) {
        long expire = -1L;
        log.info("User {} has added by an admin", dto.getUsername());
        if (dto.getDays() != -1) {
            expire = System.currentTimeMillis() + (long) dto.getDays() * 24 * 60 * 60 * 1000;
        }
        if (crackedUserService.addUser(CrackedUser.builder().password(SecureUtil.sha1(dto.getPassword())).username(dto.getUsername()).expire(expire).build())) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(RestBean.failure(409, "Conflict"), HttpStatus.CONFLICT);
    }


    @PostMapping("renew/{username}")
    public ResponseEntity<RestBean<String>> renew(@PathVariable String username, @RequestBody RenewDTO dto) {
        log.info("An admin renewed the expire date of user {} ({}d)", username, dto.getDays());
        if (crackedUserService.renew(username, dto.getDays())) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("renewAll")
    public ResponseEntity<RestBean<String>> renewAll(@RequestBody RenewDTO dto) {
        crackedUserService.renewAll(dto.getDays());
        return ResponseEntity.ok(RestBean.success("OK"));
    }

    @DeleteMapping("remove/{username}")
    public RestBean<Object> removeUser(@PathVariable String username) {
        log.info("An admin removed a user with name {}", username);
        crackedUserService.removeUser(username);
        return RestBean.success();
    }

    @PostMapping("password/{username}/reset")
    public ResponseEntity<RestBean<String>> resetPassword(@PathVariable String username, @RequestBody ResetPasswordDTO dto) {
        log.info("An admin reset the password of user {}", username);
        if (crackedUserService.resetPassword(username, dto.getPassword())) {
            return ResponseEntity.ok(RestBean.success());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Transactional
    @DeleteMapping("removeExpired")
    public RestBean<String> removeExpired() {
        crackedUserService.removeExpired();
        return RestBean.success("Success");
    }
}
