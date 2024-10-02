package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.VapeRestBean;
import fuck.manthe.nmsl.entity.dto.AuthorizationDTO;
import fuck.manthe.nmsl.entity.dto.GlobalConfigDTO;
import fuck.manthe.nmsl.entity.dto.OnlineConfigDTO;
import fuck.manthe.nmsl.entity.dto.PrivateConfigDTO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/{token}")
public class OnlineConfigController {
    @GetMapping("authenticated")
    public VapeRestBean<AuthorizationDTO> onAuthenticated(@PathVariable String token) {
        // TODO real account info
        // just return a fake account info
        return VapeRestBean.success(AuthorizationDTO.builder()
                .userId(114514)
                .username("DingZhen")
                .build());
    }

    @GetMapping("settings/load/global")
    public VapeRestBean<GlobalConfigDTO> loadGlobal(@PathVariable String token) {
        return VapeRestBean.success(GlobalConfigDTO.builder().build());
    }

    @GetMapping("settings/load/online")
    public VapeRestBean<OnlineConfigDTO> loadOnline(@PathVariable String token) {
        return VapeRestBean.success(OnlineConfigDTO.builder().build());
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
    public VapeRestBean<List<String>> publicTags(@PathVariable String token) {
        return VapeRestBean.success(new ArrayList<>()); // stage 23
    }

    /**
     * Private config
     */
    @GetMapping("profile/private/all")
    public VapeRestBean<PrivateConfigDTO> privateConfig(@PathVariable String token) {
        return VapeRestBean.success(PrivateConfigDTO.builder()
                .friends(new ArrayList<>())
                .profiles(new HashMap<>())
                .build());
    }
}
