package fuck.manthe.nmsl.service.impl;

import com.alibaba.fastjson2.JSON;
import fuck.manthe.nmsl.entity.Gateway;
import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;
import fuck.manthe.nmsl.entity.vo.ColdDownVO;
import fuck.manthe.nmsl.entity.vo.GatewayAuthorizeVO;
import fuck.manthe.nmsl.repository.GatewayRepository;
import fuck.manthe.nmsl.service.GatewayService;
import fuck.manthe.nmsl.service.VapeAccountService;
import fuck.manthe.nmsl.util.Const;
import fuck.manthe.nmsl.util.CryptoUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
public class GatewayServiceImpl implements GatewayService {
    @Resource
    RedisTemplate<String, Long> redisTemplate;

    @Value("${service.mode}")
    String mode;

    @Value("${service.gateway.key}")
    String gatewayKey;

    @Value("${service.gateway.always}")
    boolean alwaysEnableGateway;

    @Resource
    GatewayRepository gatewayRepository;

    @Resource
    VapeAccountService vapeAccountService;

    @Resource
    OkHttpClient httpClient;

    @Resource
    CryptoUtil cryptoUtil;

    private final String secretText = "Hello World";

    @PostConstruct
    public void init() {
        if (isPureGateway() || alwaysEnableGateway) {
            log.info("Servlet is running in gateway mode.");
            if (alwaysEnableGateway) {
                log.info("Current servlet mode is {}, but service.gateway.always is true.", mode);
            }
            log.warn("Gateway key: {}", gatewayKey);
            log.warn("DO NOT share your gateway key with anybody, otherwise your account will be hacked");
        } else if (!canUseGateway()) {
            log.info("Gateways are disabled via application.yml, no gateways will be used for fetching tokens");
        }
    }


    @Override
    public boolean isPureGateway() {
        return mode.equals("gateway");
    }

    @Override
    public boolean canUseGateway() {
        return mode.equals("full");
    }

    @Override
    public Gateway addGateway(Gateway gateway) {
        // 由于考虑到可能用户会自己编写服务端来实现刷新账号的逻辑,所以此处不判断重复gateway
        Gateway saved = gatewayRepository.save(gateway);
        log.info("Gateway {} was added (name=\"{}\", address=\"{}\")", saved.getId(), saved.getName(), gateway.getAddress());
        return saved;
    }

    @Override
    public boolean removeGateway(String id) {
        if (!gatewayRepository.existsById(id)) return false;
        gatewayRepository.deleteById(id);
        log.info("Gateway {} was deleted", id);
        return true;
    }

    @Override
    public Gateway getOne() {
        List<Gateway> all = gatewayRepository.findAll();
        for (Gateway gateway : all) {
            if (!isColdDown(gateway)) {
                log.info("Gateway \"{}\" will be used to fetch token.", gateway.getName());
                // 在请求完成后再进行冷却
                return gateway;
            }
        }
        return null;
    }

    @Override
    public VapeAuthorizeDTO use(Gateway gateway) throws Exception {
        try (Response response = httpClient.newCall(new Request.Builder()
                .get()
                .url(new URL(gateway.getAddress() + "/gateway/token"))
                .header("X-Gateway-Secret", cryptoUtil.encrypt(secretText, cryptoUtil.toKey(gateway.getKey())))
                .build()).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String json = response.body().string();
                GatewayAuthorizeVO vo = JSON.parseObject(json, GatewayAuthorizeVO.class);
                markColdDown(gateway, vo.getColdDown().getTime());
                return this.processDecrypt(gateway, vo);
            } else {
                if (!response.isSuccessful()) {
                    log.error("Gateway {} responded {}", gateway.getName(), response.code());
                }
                log.error("Gateway {} ({}) responded an invalid response. (empty body)", gateway.getName(), gateway.getId());
                return null;
            }
        }
    }

    @Override
    public List<Gateway> list() {
        return gatewayRepository.findAll();
    }

    @Override
    public Gateway findGatewayById(String id) {
        return gatewayRepository.findById(id).orElse(null);
    }

    @Override
    public Gateway saveGateway(Gateway gateway) {
        return gatewayRepository.save(gateway);
    }

    @Override
    public GatewayAuthorizeVO processEncrypt(VapeAuthorizeDTO dto) throws Exception {
        return GatewayAuthorizeVO.builder()
                .token(cryptoUtil.encryptGateway(dto.getToken()))
                .status(dto.getStatus())
                .coldDown(ColdDownVO.builder()
                        .time(vapeAccountService.calculateColdDown())
                        .build()
                )
                .build();
    }

    @Override
    public VapeAuthorizeDTO processDecrypt(Gateway gateway, GatewayAuthorizeVO vo) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String token = cryptoUtil.decrypt(vo.getToken(), cryptoUtil.toKey(gateway.getKey()));
        return VapeAuthorizeDTO.builder()
                .status(vo.getStatus())
                .token(token)
                .build();
    }

    @Override
    public boolean assertSecret(String providedSecret) {
        try {
            return Objects.equals(cryptoUtil.decryptGateway(providedSecret), secretText);
        } catch (Exception e) {
            log.error("Failed to verify gateway secret, have you configured correctly?");
            log.error("Encrypted secret: {}", providedSecret);
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void markColdDown(Gateway gateway, long expireAt) {
        // 具体事件在gateway服务端计算
        redisTemplate.opsForValue().set(Const.GATEWAY_COLD_DOWN + gateway.getId(), expireAt);
    }

    @Override
    public boolean isColdDown(Gateway gateway) {
        // 请确保两个服务器时间都是准确的
        return getColdDown(gateway) > System.currentTimeMillis();
    }

    @Override
    public long getColdDown(Gateway gateway) {
        Long l = redisTemplate.opsForValue().get(Const.GATEWAY_COLD_DOWN + gateway.getId());
        if (l == null) return 0L;
        return l;
    }
}
