package fuck.manthe.nmsl.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import fuck.manthe.nmsl.entity.Gateway;
import fuck.manthe.nmsl.entity.GatewayHeartbeatInfo;
import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;
import fuck.manthe.nmsl.entity.vo.ColdDownVO;
import fuck.manthe.nmsl.entity.vo.GatewayAuthorizeVO;
import fuck.manthe.nmsl.entity.vo.GatewayHeartbeatVO;
import fuck.manthe.nmsl.entity.webhook.GatewayHeartbeatFailedMessage;
import fuck.manthe.nmsl.repository.GatewayHeartbeatInfoRepository;
import fuck.manthe.nmsl.repository.GatewayRepository;
import fuck.manthe.nmsl.service.GatewayService;
import fuck.manthe.nmsl.service.VapeAccountService;
import fuck.manthe.nmsl.service.WebhookService;
import fuck.manthe.nmsl.util.Const;
import fuck.manthe.nmsl.util.CryptoUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.Optional;

@Service
@Log4j2
public class GatewayServiceImpl implements GatewayService {
    private final String secretText = "Hello World";

    @Resource
    RedisTemplate<String, Long> redisTemplate;

    @Value("${service.mode}")
    String mode;

    @Value("${service.gateway.key}")
    String gatewayKey;

    @Value("${service.gateway.always}")
    boolean alwaysEnableGateway;

    @Value("${service.gateway.heartbeat.state}")
    boolean heartbeatState;

    @Resource
    GatewayRepository gatewayRepository;

    @Resource
    GatewayHeartbeatInfoRepository gatewayHeartbeatInfoRepository;

    @Resource
    VapeAccountService vapeAccountService;

    @Resource
    OkHttpClient httpClient;

    @Resource
    CryptoUtil cryptoUtil;

    @Resource
    WebhookService webhookService;

    @PostConstruct
    public void init() {
        if (isGatewayEnabled()) {
            log.info("Servlet is running in gateway mode.");
            if (alwaysEnableGateway) {
                log.info("Current servlet mode is {}, but service.gateway.always is true.", mode);
            }
            log.warn("Gateway key: {}", gatewayKey);
            log.warn("DO NOT share your gateway key with anybody, otherwise your account will be hacked");
        } else if (!canUseGateway()) {
            log.info("Gateways are disabled via application.yml, no gateways will be used for fetching tokens");
        } else if (heartbeatState) {
            log.info("Heartbeat packets will be send in every 5 minutes.");
        }
    }


    @Override
    public boolean isPureGateway() {
        return mode.equals("gateway");
    }

    @Override
    public boolean isGatewayEnabled() {
        return this.isPureGateway() || alwaysEnableGateway;
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
        heartbeat(saved); // update status
        return saved;
    }

    @Override
    public boolean removeGateway(String id) {
        if (!gatewayRepository.existsById(id)) return false;
        gatewayHeartbeatInfoRepository.deleteAllByGateway(id);
        gatewayRepository.deleteById(id);
        log.info("Gateway {} was deleted", id);
        return true;
    }

    @Override
    public Gateway getOne() {
        List<Gateway> all = gatewayRepository.findAll().stream().filter(Gateway::isEnabled).toList();
        for (Gateway gateway : all) {
            if (!isColdDown(gateway) && !isAvailableNoPing(gateway)) {
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
    @SneakyThrows
    public boolean heartbeat(Gateway gateway) {
        GatewayHeartbeatInfo heartbeatInfo = new GatewayHeartbeatInfo();
        heartbeatInfo.setGateway(gateway.getId());
        boolean result = false;
        try (Response response = httpClient.newCall(new Request.Builder()
                .get()
                .url(new URL(gateway.getAddress() + "/gateway/heartbeat"))
                .header("X-Gateway-Secret", cryptoUtil.encrypt(secretText, cryptoUtil.toKey(gateway.getKey())))
                .build()).execute()) {
            if (response.isSuccessful()) {
//                log.info("Gateway {} is alive", gateway.getName());
                assert response.body() != null;
                String responseString = response.body().string();
                GatewayHeartbeatVO heartbeat = JSONObject.parseObject(responseString, GatewayHeartbeatVO.class);
                // sync implementation
                if (!gateway.getImplementation().equals(heartbeat.getImplementation())) {
                    gateway.setImplementation(heartbeat.getImplementation());
                    gatewayRepository.save(gateway);
                }
                // sync colddown
//                log.debug("Sync cold down for gateway {} ({})", gateway.getName(), heartbeat.getColdDown().getTime());
                markColdDown(gateway, heartbeat.getColdDown().getTime());
                result = true;
                heartbeatInfo.setStatus(GatewayHeartbeatInfo.Status.OK);
            } else if (response.code() == 403) {
                log.error("Failed to send heartbeat to Gateway {} (incorrect key)", gateway.getName());
                heartbeatInfo.setStatus(GatewayHeartbeatInfo.Status.BAD_KEY);
            } else if (response.code() == 400) {
                log.error("Gateway {} is unavailable (400)", gateway.getName());
                heartbeatInfo.setStatus(GatewayHeartbeatInfo.Status.BAD_REQUEST);
            } else {
                log.warn("Gateway {} has not implemented the heartbeat API", gateway.getName());
                heartbeatInfo.setStatus(GatewayHeartbeatInfo.Status.UNIMPLEMENTED_API);
            }
        } catch (Exception e) {
            log.error("Failed to send heartbeat to {}", gateway.getName());
            heartbeatInfo.setStatus(GatewayHeartbeatInfo.Status.INTERNAL_ERROR);
        } finally {
            gatewayHeartbeatInfoRepository.save(heartbeatInfo);
        }

        if (result != isAvailableNoPing(gateway)) {
            GatewayHeartbeatFailedMessage message = new GatewayHeartbeatFailedMessage();
            message.setGateway(gateway.getId());
            message.setTimestamp(System.currentTimeMillis() / 1000L);
            if (result) {
                log.info("Gateway {} is now available.", gateway.getName());
                message.setContent(String.format("密钥刷新服务 %s 已上线", gateway.getName()));
            } else {
                log.info("Gateway {} went offline.", gateway.getName());
                message.setContent(String.format("密钥刷新服务 %s 已离线,请查看Log获取更多信息", gateway.getName()));
            }
            webhookService.pushAll("gateway-heartbeat", message);
        }
        return result;
    }

    @Override
    public boolean isAvailable(Gateway gateway) {
        Optional<GatewayHeartbeatInfo> heartbeatInfo = gatewayHeartbeatInfoRepository.findFirstByGatewayOrderByCreateAtDesc(gateway.getId());
        return heartbeatInfo.map(GatewayHeartbeatInfo::isAvailable).orElseGet(() -> heartbeat(gateway));
    }

    private boolean isAvailableNoPing(Gateway gateway) {
        return gatewayHeartbeatInfoRepository.findFirstByGatewayOrderByCreateAtDesc(gateway.getId()).map(GatewayHeartbeatInfo::isAvailable).orElse(false);
    }

    @Override
    public Gateway toggle(Gateway gateway, boolean state) {
        gateway.setEnabled(state);
        log.info("Gateway {} is now {}", gateway.getName(), (state) ? "enabled" : "disabled");
        return gatewayRepository.save(gateway);
    }

    @Scheduled(cron = "0 */5 * * * *")
    private void sendHeartbeat() {
        if (!heartbeatState || isGatewayEnabled()) {
            return;
        }
        log.info("Sending heartbeat to Gateways...");
        for (Gateway gateway : list().stream().filter(Gateway::isEnabled).toList()) {
            log.info("Sending gateway heartbeat to {}", gateway.getName());
            heartbeat(gateway);
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

    @Override
    public List<GatewayHeartbeatInfo> status(Gateway gateway) {
        return gatewayHeartbeatInfoRepository.findAllByGateway(gateway.getId());
    }
}
