package fuck.manthe.nmsl;

import fuck.manthe.nmsl.entity.User;
import fuck.manthe.nmsl.service.OnlineConfigService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DingZhenServletApplicationTests {
    @Resource
    OnlineConfigService onlineConfigService;

    @Test
    void contextLoads() {
    }

    @Test
    void onlineToken() {
        String token = "123";
        onlineConfigService.cache(token, "admin");
        User cachedUser = onlineConfigService.findByToken(token);
        assert cachedUser != null && cachedUser.getUsername().equals("admin");
    }
}
