package fuck.manthe.nmsl.controller.admin;

import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.User;
import fuck.manthe.nmsl.entity.dto.AddUserDTO;
import fuck.manthe.nmsl.entity.dto.RenewDTO;
import fuck.manthe.nmsl.entity.dto.ResetPasswordDTO;
import fuck.manthe.nmsl.entity.dto.UserDTO;
import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/admin/user")
public class UserManageController {
    @Resource
    UserService userService;

    @Resource
    AnalysisService analysisService;

    @Resource
    PasswordEncoder passwordEncoder;

    @GetMapping("list")
    public List<UserDTO> listUsers() {
        return userService.list().stream().map((user) -> UserDTO.builder()
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
        if (userService.addUser(User.builder().password(passwordEncoder.encode(dto.getPassword())).username(dto.getUsername()).expire(expire).build())) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(RestBean.failure(409, "Conflict"), HttpStatus.CONFLICT);
    }


    @PostMapping("renew/{username}")
    public ResponseEntity<RestBean<String>> renew(@PathVariable String username, @RequestBody RenewDTO dto) {
        log.info("An admin renewed the expire date of user {} ({}d)", username, dto.getDays());
        if (userService.renew(username, dto.getDays())) {
            return ResponseEntity.ok(RestBean.success("OK"));
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("renewAll")
    public ResponseEntity<RestBean<String>> renewAll(@RequestBody RenewDTO dto) {
        userService.renewAll(dto.getDays());
        return ResponseEntity.ok(RestBean.success("OK"));
    }

    @DeleteMapping("remove/{username}")
    public RestBean<Object> removeUser(@PathVariable String username) {
        log.info("An admin removed a user with name {}", username);
        userService.removeUser(username);
        return RestBean.success();
    }

    @PostMapping("password/{username}/reset")
    public ResponseEntity<RestBean<String>> resetPassword(@PathVariable String username, @RequestBody ResetPasswordDTO dto) {
        log.info("An admin reset the password of user {}", username);
        if (userService.resetPassword(username, dto.getPassword())) {
            return ResponseEntity.ok(RestBean.success());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("removeExpired")
    public RestBean<String> removeExpired() {
        userService.removeExpired();
        return RestBean.success("Success");
    }
}
