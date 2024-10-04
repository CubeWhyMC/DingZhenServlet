package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.User;
import fuck.manthe.nmsl.entity.VapeRestBean;
import fuck.manthe.nmsl.entity.dto.AuthorizationDTO;
import fuck.manthe.nmsl.entity.dto.GlobalConfigDTO;
import fuck.manthe.nmsl.entity.dto.OnlineConfigDTO;
import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.service.OnlineConfigService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/{token}")
public class OnlineConfigController {
    @Resource
    OnlineConfigService onlineConfigService;

    @Resource
    AnalysisService analysisService;

    @GetMapping("authenticated")
    public VapeRestBean<AuthorizationDTO> onAuthenticated(@PathVariable String token) {
        User user = onlineConfigService.findByToken(token);
        return VapeRestBean.success(AuthorizationDTO.builder()
                .accountCreation(formatVapeTime(user.getRegisterTime()))
                .userId(114514)
                .username(user.getUsername())
                .build());
    }

    @NotNull
    private String formatVapeTime(@NotNull LocalDateTime date) {
        Instant instant = date.toInstant(ZoneOffset.UTC);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return zonedDateTime.format(formatter);
    }

    @GetMapping("settings/load/global")
    public VapeRestBean<GlobalConfigDTO> loadGlobal(@PathVariable String token) {
        User user = onlineConfigService.findByToken(token);
        return VapeRestBean.success(GlobalConfigDTO.builder()
                .cache(true)
                .firstRun(analysisService.getLastLaunch(user.getUsername()) == -1)
                .build());
    }

    @GetMapping("settings/load/online")
    public VapeRestBean<OnlineConfigDTO> loadOnline(@PathVariable String token, HttpServletResponse response) throws IOException {
        return VapeRestBean.success(OnlineConfigDTO.builder()
                .autoLogin(true)
                .friendStates(new HashMap<>())
                .inventorySwitchMode(0)
                .partyShowTarget(true)
                .pingKeybind(new ArrayList<>())
                .shareInventory(true)
                .showSelf(true)
                .showServer(true)
                .showUsername(true)
                .build());
    }

    @PostMapping("settings/save/online")
    public VapeRestBean<OnlineConfigDTO> saveOnline(@PathVariable String token, @RequestBody OnlineConfigDTO onlineConfig) {
        // todo save config
        return VapeRestBean.success(onlineConfig);
    }

    /**
     * Public profiles
     */
    @GetMapping("profile/public/tags")
    public void /* VapeRestBean<List<String>> */ publicTags(@PathVariable String token, HttpServletResponse response) throws IOException {
        // return VapeRestBean.success(new ArrayList<>()); // stage 23
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getOutputStream().write(Objects.requireNonNull(this.getClass().getResourceAsStream("/fake-data/fake-pub-tags.json")).readAllBytes());
    }

    /**
     * Private config
     */
    @GetMapping("profile/private/all")
    public void /*VapeRestBean<PrivateConfigDTO>*/ privateConfig(@PathVariable String token, HttpServletResponse response) throws IOException {
//        return VapeRestBean.success(PrivateConfigDTO.builder()
//                .friends(new ArrayList<>())
//                .profiles(new HashMap<>())
//                .build());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getOutputStream().write(Objects.requireNonNull(this.getClass().getResourceAsStream("/fake-data/fake-private-config.json")).readAllBytes());
    }
}
