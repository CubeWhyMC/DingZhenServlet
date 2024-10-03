package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.VapeRestBean;
import fuck.manthe.nmsl.entity.dto.AuthorizationDTO;
import fuck.manthe.nmsl.entity.dto.GlobalConfigDTO;
import fuck.manthe.nmsl.entity.dto.OnlineConfigDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/{token}")
public class OnlineConfigController {
    @GetMapping("authenticated")
    public VapeRestBean<AuthorizationDTO> onAuthenticated(@PathVariable String token) {
        // TODO real account info
        // just return a fake account info
        return VapeRestBean.success(AuthorizationDTO.builder()
                .accountCreation(formatVapeTime(new Date(0))) // 1970/1/1
                .userId(114514)
                .username("getvape-today")
                .build());
    }

    private String formatVapeTime(Date date) {
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return zonedDateTime.format(formatter);
    }

    @GetMapping("settings/load/global")
    public VapeRestBean<GlobalConfigDTO> loadGlobal(@PathVariable String token) {
        return VapeRestBean.success(GlobalConfigDTO.builder().build());
    }

    @GetMapping("settings/load/online")
    public void /*VapeRestBean<OnlineConfigDTO>*/ loadOnline(@PathVariable String token, HttpServletResponse response) throws IOException {
//        return VapeRestBean.success(OnlineConfigDTO.builder().build());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getOutputStream().write(Objects.requireNonNull(this.getClass().getResourceAsStream("/fake-data/fake-online-config.json")).readAllBytes());
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
