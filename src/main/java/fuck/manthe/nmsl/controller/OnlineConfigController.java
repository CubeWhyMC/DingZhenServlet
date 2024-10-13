package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.*;
import fuck.manthe.nmsl.entity.dto.*;
import fuck.manthe.nmsl.entity.vo.*;
import fuck.manthe.nmsl.service.OnlineConfigService;
import fuck.manthe.nmsl.util.FormatUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Log4j2
@RestController
@RequestMapping("/api/v1/{token}")
public class OnlineConfigController {
    @Resource
    OnlineConfigService onlineConfigService;

    @Resource
    FormatUtil formatUtil;

    @Value("${share.online.search.state}")
    boolean isSearchEnabled;

    @Value("${share.online.search.page-size}")
    int pageSize;

    @GetMapping("authenticated")
    public VapeRestBean<AuthorizationDTO> onAuthenticated(@PathVariable String token) {
        User user = onlineConfigService.findByToken(token);
        if (user == null) {
            log.error("Failed to find user with the online token {}", token);
            return VapeRestBean.success(
                    AuthorizationDTO.builder()
                            .accountCreation(formatUtil.formatVapeTime(new Date()))
                            .userId(114514)
                            .username("getvape-today")
                            .build()
            ); // return fake data
        }
        log.info("User {} login to the fake online service (token={})", user.getUsername(), token);
        return VapeRestBean.success(AuthorizationDTO.builder()
                .accountCreation(formatUtil.formatVapeTime(user.getRegisterTime()))
                .userId(114514)
                .username(user.getUsername())
                .build());
    }

    @GetMapping("settings/load/global")
    public VapeRestBean<GlobalConfigVO> loadGlobal(@PathVariable String token) {
        return VapeRestBean.success(onlineConfigService.loadGlobal(token).asViewObject(GlobalConfigVO.class));
    }

    @PostMapping("settings/save/global")
    public VapeRestBean<GlobalConfigVO> saveGlobal(@PathVariable String token, @RequestBody GlobalConfigDTO dto) {
        return VapeRestBean.success(onlineConfigService.saveGlobal(token, GlobalConfig.builder()
                .cache(dto.isCache())
                .firstRun(dto.isFirstRun())
                .build()).asViewObject(GlobalConfigVO.class));
    }

    @GetMapping("settings/load/online")
    public VapeRestBean<OnlineConfigVO> loadOnline(@PathVariable String token) {
        return VapeRestBean.success(onlineConfigService.loadOnline(token).asViewObject(OnlineConfigVO.class));
    }

    @PostMapping("settings/save/online")
    public VapeRestBean<OnlineConfigVO> saveOnline(@PathVariable String token, @RequestBody OnlineConfigDTO dto) {
        OnlineConfig cfg = onlineConfigService.saveOnline(token, OnlineConfig.builder()
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
        return VapeRestBean.success(cfg.asViewObject(OnlineConfigVO.class));
    }

    /**
     * Public profiles
     * stage 21
     */
    @GetMapping("profile/public/tags")
    public VapeRestBean<List<String>> publicTags(@PathVariable String token) {
        // 不知道这个是干什么的
        return VapeRestBean.success(onlineConfigService.loadPublicTags());
    }

    /**
     * Private config
     */
    @GetMapping("profile/private/all")
    public VapeRestBean<PrivateProfileVO> privateConfig(@PathVariable String token, HttpServletResponse response) throws IOException {
        PrivateProfile profile = onlineConfigService.loadPrivateProfile(token);
        if (profile == null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getOutputStream().write(Objects.requireNonNull(this.getClass().getResourceAsStream("/fake-data/fake-private-config.json")).readAllBytes());
            return null;
        }
        List<CheatProfile> savedProfiles = onlineConfigService.loadSavedProfiles(token);
        Map<String, CheatProfileVO> voMap = new HashMap<>();
        savedProfiles.forEach((cheatProfile) -> voMap.put(cheatProfile.getId(), CheatProfileVO.fromCheatProfile(cheatProfile)));
        PrivateProfileVO vo = PrivateProfileVO.builder()
                .friends(profile.getFriends())
                .profiles(voMap)
                .publicProfiles(new HashMap<>()) // TODO public profiles
                .otherData(profile.getOtherData())
                .build();
        return VapeRestBean.success(vo);
    }

    @PostMapping("profile/private/save/profile/")
    public VapeRestBean<Map<String, CheatProfileVO>> savePrivateProfile(@PathVariable String token, @RequestBody SavePrivateProfileDTO dto) {
        List<UpdatePrivateProfileDTO> updatedProfiles = dto.getUpdatedProfiles();
        List<String> deletedProfiles = dto.getDeletedProfiles();
        onlineConfigService.updateCheatProfiles(token, updatedProfiles);
        onlineConfigService.deleteCheatProfiles(token, deletedProfiles);
        List<CheatProfile> profiles = onlineConfigService.loadSavedProfiles(token);
        Map<String, CheatProfileVO> voMap = new HashMap<>();
        for (CheatProfile profile : profiles) {
            voMap.put(profile.getId(), CheatProfileVO.fromCheatProfile(profile));
        }
        return VapeRestBean.success(voMap);
    }

    @PostMapping("profile/private/save/user/")
    public VapeRestBean<Object> updatePreferences(@PathVariable String token, @RequestBody UpdateOnlinePreferencesDTO dto) {
        onlineConfigService.updatePreferences(token, dto);
        return VapeRestBean.success();
    }

    @PostMapping("profile/public/list")
    public VapeRestBean<SearchCheatProfileResultVO> searchProfile(@PathVariable String token, @RequestBody SearchCheatProfileDTO dto) {
        if (!isSearchEnabled) {
            return VapeRestBean.success(
                    SearchCheatProfileResultVO.builder()
                            .content(List.of())
                            .size(pageSize)
                            .last(true)
                            .numberOfElements(0)
                            .totalElements(0)
                            .totalPages(0)
                            .build()
            );
        }
        List<CheatProfile> profiles = onlineConfigService.searchProfiles(dto.getSearch());
        List<CheatProfile> subList = profiles.subList(0, pageSize);
        List<SearchResultContentVO> results = new ArrayList<>();
        for (CheatProfile profile : subList) {
            results.add(SearchResultContentVO.builder()
                    .tags(new ArrayList<>()) // TODO search tags
                    .name(profile.getName())
                    .shareCode(profile.getShareCode())
                    .owner(OwnerVO.builder()
                            .userId(114514)
                            .username(profile.getOwner().getUsername())
                            .build())
                    .build());
        }
        return VapeRestBean.success(SearchCheatProfileResultVO.builder()
                .content(results)
                .size(pageSize)
                .last(profiles.size() < pageSize || subList.size() < pageSize || (subList.size() == pageSize && profiles.size() % pageSize == 1))
                .totalElements(profiles.size())
                .numberOfElements(profiles.size())
                .build());
    }
}
