package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.GlobalConfig;
import fuck.manthe.nmsl.entity.OnlineConfig;
import fuck.manthe.nmsl.entity.User;
import fuck.manthe.nmsl.entity.VapeRestBean;
import fuck.manthe.nmsl.entity.dto.AuthorizationDTO;
import fuck.manthe.nmsl.entity.dto.GlobalConfigDTO;
import fuck.manthe.nmsl.entity.dto.OnlineConfigDTO;
import fuck.manthe.nmsl.entity.vo.GlobalConfigVO;
import fuck.manthe.nmsl.entity.vo.OnlineConfigVO;
import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.service.OnlineConfigService;
import fuck.manthe.nmsl.service.UserService;
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
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/{token}")
public class OnlineConfigController {
    @Resource
    OnlineConfigService onlineConfigService;

    @Resource
    AnalysisService analysisService;

    @Resource
    UserService userService;

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
    public VapeRestBean<GlobalConfigVO> loadGlobal(@PathVariable String token) {
        User user = onlineConfigService.findByToken(token);
        GlobalConfig globalConfig = user.getGlobalConfig();
        if (globalConfig == null) {
            globalConfig = GlobalConfig.builder()
                    .cache(true)
                    .firstRun(analysisService.getLastLaunch(user.getUsername()) == -1)
                    .build();
        }
        return VapeRestBean.success(globalConfig.asViewObject(GlobalConfigVO.class));
    }

    @PostMapping("settings/save/global")
    public VapeRestBean<GlobalConfigVO> saveGlobal(@PathVariable String token, @RequestBody GlobalConfigDTO dto) {
        User user = onlineConfigService.findByToken(token);
        user.setGlobalConfig(GlobalConfig.builder()
                .cache(dto.isCache())
                .firstRun(dto.isFirstRun())
                .build());
        return VapeRestBean.success(userService.save(user).getGlobalConfig().asViewObject(GlobalConfigVO.class));
    }

    @GetMapping("settings/load/online")
    public VapeRestBean<OnlineConfigVO> loadOnline(@PathVariable String token) {
        User user = onlineConfigService.findByToken(token);
        OnlineConfig onlineConfig = user.getOnlineConfig();
        if (onlineConfig == null) {
            onlineConfig = OnlineConfig.DEFAULT; // first launch
        }
        return VapeRestBean.success(onlineConfig.asViewObject(OnlineConfigVO.class));
    }

    @PostMapping("settings/save/online")
    public VapeRestBean<OnlineConfigVO> saveOnline(@PathVariable String token, @RequestBody OnlineConfigDTO dto) {
        User user = onlineConfigService.findByToken(token);
        user.setOnlineConfig(OnlineConfig.builder()
                .autoLogin(dto.isAutoLogin())
                .friendStates(dto.getFriendStates())
                .pingKeybind(dto.getPingKeybind())
                .shareInventory(dto.isShareInventory())
                .showInventoryKeybind(dto.getShowInventoryKeybind())
                .showSelf(dto.isShowSelf())
                .showServer(dto.isShowServer())
                .showUsername(dto.isShowUsername())
                .inventorySwitchMode(dto.getInventorySwitchMode())
                .build());
        return VapeRestBean.success(userService.save(user).getOnlineConfig().asViewObject(OnlineConfigVO.class));
    }

    /**
     * Public profiles
     * stage 21
     */
    @GetMapping("profile/public/tags")
    public VapeRestBean<List<String>> publicTags(@PathVariable String token) {
        // 不知道这个是干什么的
        ArrayList<String> tags = new ArrayList<>();
        tags.add("getvape.today");
        tags.add("Hypixel");
        tags.add("Manthe");
        tags.add("#VapeClient");
        return VapeRestBean.success(tags);
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
