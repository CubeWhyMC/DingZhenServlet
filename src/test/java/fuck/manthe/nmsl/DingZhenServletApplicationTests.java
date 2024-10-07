package fuck.manthe.nmsl;

import fuck.manthe.nmsl.util.CryptoUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DingZhenServletApplicationTests {
//    @Resource
//    OnlineConfigService onlineConfigService;

    @Resource
    CryptoUtil cryptoUtil;

    @Test
    void contextLoads() {
    }

//    @Test
//    void onlineToken() {
//        String token = "12345";
//        onlineConfigService.cache(token, "admin");
//        User cachedUser = onlineConfigService.findByToken(token);
//        assert cachedUser != null && cachedUser.getUsername().equals("admin");
//    }

    @Test
    void gatewayCrypto() throws Exception {
        String text = "/.1;!@#$%^&*()\"}FUCKYOU MANTHE{|||\\asAaa><<<)&(|";
        String encrypted = cryptoUtil.encryptGateway(text);
        String decrypted = cryptoUtil.decryptGateway(encrypted);
        assert decrypted.equals(text);
    }
}
