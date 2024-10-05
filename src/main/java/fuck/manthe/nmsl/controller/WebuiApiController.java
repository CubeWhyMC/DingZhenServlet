package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.CheatProfile;
import fuck.manthe.nmsl.entity.dto.LoadWebProfileDTO;
import fuck.manthe.nmsl.entity.vo.CheatProfileVO;
import fuck.manthe.nmsl.service.OnlineConfigService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Objects;

@RestController
@RequestMapping("/api/v2")
public class WebuiApiController {
    @Resource
    OnlineConfigService onlineConfigService;

    @RequestMapping("ping")
    public String ping(Principal principal) {
        return "pong! Hello " + principal.getName();
    }

    @GetMapping("config")
    public ResponseEntity<CheatProfileVO> config(Principal principal, @RequestBody LoadWebProfileDTO dto) {
        CheatProfile profile = onlineConfigService.findProfileByUuid(dto.getUuid());
        if (!Objects.equals(profile.getOwner().getUsername(), principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(CheatProfileVO.builder()
                .profileId(profile.getId())
                .uuid(profile.getUuid())
                .name(profile.getName())
                .created(profile.getCreated())
                .data(profile.getData())
                .lastUpdated(profile.getLastUpdated())
                .ownerId(114514)
                .vapeVersion(profile.getVapeVersion())
                .lastUpdated(profile.getLastUpdated())
                .metadata(profile.getMetadata())
                .build());
    }
}
