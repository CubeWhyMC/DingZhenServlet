package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.CheatProfile;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.User;
import fuck.manthe.nmsl.entity.vo.CheatProfileVO;
import fuck.manthe.nmsl.service.OnlineConfigService;
import fuck.manthe.nmsl.service.UserService;
import fuck.manthe.nmsl.util.FormatUtil;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Log4j2
@RestController
@RequestMapping("/api/v2")
public class WebuiApiController {
    @Resource
    OnlineConfigService onlineConfigService;

    @Resource
    UserService userService;

    @Resource
    FormatUtil formatUtil;

    @RequestMapping("ping")
    public String ping(Principal principal) {
        return "pong! Hello " + principal.getName();
    }

    @GetMapping("config")
    public ResponseEntity<CheatProfileVO> config(Principal principal, @RequestParam String uuid) {
        CheatProfile profile = onlineConfigService.findProfileByUuid(uuid);
        if (!Objects.equals(profile.getOwner().getUsername(), principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        log.info("Load profile UUID:{} (by {})", uuid, profile.getOwner().getUsername());
        return ResponseEntity.ok(CheatProfileVO.fromCheatProfile(profile));
    }

    @PostMapping("config")
    public ResponseEntity<RestBean<Object>> config(Principal principal, @RequestBody CheatProfileVO dto) {
        String id = dto.getProfileId();
        CheatProfile profile = onlineConfigService.findProfileById(id);
        if (!Objects.equals(profile.getOwner().getUsername(), principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(RestBean.forbidden("Forbidden"));
        }
        profile.setLastUpdated(formatUtil.formatVapeTime(new Date()));
        profile.setData(dto.getData());
        profile.setMetadata(dto.getMetadata());
        profile.setVapeVersion(dto.getVapeVersion());
        profile.setUuid(dto.getUuid());
        profile.setName(dto.getName());

        CheatProfile updated = onlineConfigService.updateCheatProfile(profile);
        log.info("Profile UUID:{} updated.", updated.getUuid());
        return ResponseEntity.ok(RestBean.success());
    }

    @GetMapping("config/list")
    public ResponseEntity<List<CheatProfileVO>> configList(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<CheatProfileVO> vos = new ArrayList<>();
        user.getPrivateProfile().getProfiles().forEach((id, uuid) -> {
            vos.add(CheatProfileVO.fromCheatProfile(onlineConfigService.findProfileById(id)));
        });
        return ResponseEntity.ok(vos);
    }
}
