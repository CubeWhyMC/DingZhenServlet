package fuck.manthe.nmsl.service.impl;

import com.alibaba.fastjson2.JSON;
import com.standardwebhooks.Webhook;
import com.standardwebhooks.exceptions.WebhookSigningException;
import fuck.manthe.nmsl.entity.BaseWebhookMessage;
import fuck.manthe.nmsl.entity.WebhookEndpoint;
import fuck.manthe.nmsl.repository.WebhookEndpointRepository;
import fuck.manthe.nmsl.service.WebhookService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class WebhookServiceImpl implements WebhookService {
    @Resource
    OkHttpClient okHttpClient;

    @Resource
    WebhookEndpointRepository webhookEndpointRepository;

    @Value("${share.webhook.state}")
    boolean webhookState;

    @PostConstruct
    public void init() {
        if (webhookState) {
            log.info("Webhooks are enabled.");
        } else {
            log.info("Webhooks are disabled.");
        }
    }

    @Override
    public WebhookEndpoint add(String name, String url, String secret) {
        WebhookEndpoint entity = new WebhookEndpoint();
        entity.setName(name);
        entity.setUrl(url);
        entity.setSecret(secret);
        return webhookEndpointRepository.save(entity);
    }

    @Override
    public WebhookEndpoint find(long id) {
        return webhookEndpointRepository.findById(id).orElse(null);
    }

    @Override
    public List<WebhookEndpoint> list() {
        return webhookEndpointRepository.findAll();
    }

    @Override
    public void remove(long id) {
        webhookEndpointRepository.deleteById(id);
    }

    @Override
    public WebhookEndpoint update(WebhookEndpoint entity) {
        return webhookEndpointRepository.save(entity);
    }

    @Override
    public boolean push(WebhookEndpoint endpoint, String msgId, String payload) throws WebhookSigningException {
        if (!webhookState) {
            return false;
        }
        log.info("Pushing event {} to webhook {}", msgId, endpoint.getName());
        Webhook webhook = new Webhook(endpoint.getSecret());
        // sign payload
        long now = System.currentTimeMillis() / Webhook.SECOND_IN_MS;
        String sign = webhook.sign(msgId, now, payload);
        try (Response execute = okHttpClient.newCall(new Request.Builder()
                .url(endpoint.getUrl())
                .post(RequestBody.create(payload, MediaType.parse("application/json")))
                .header("webhook-id", msgId)
                .header("webhook-signature", sign)
                .header("webhook-timestamp", String.valueOf(now))
                .build()).execute()) {
            if (execute.isSuccessful()) {
                log.info("Successfully pushed event {} to webhook {}", msgId, endpoint.getName());
                if (execute.body() != null) {
                    log.debug(execute.body().string());
                }
                return true;
            }
        } catch (Exception e) {
            log.error("Failed to push event {} to webhook {}", msgId, endpoint.getName());
            log.error(e.getMessage(), e);
            return false;
        }
        return false;
    }

    @Override
    public void pushAll(String msgId, BaseWebhookMessage payload) throws WebhookSigningException {
        List<WebhookEndpoint> endpoints = list();
        for (WebhookEndpoint endpoint : endpoints) {
            try {
                push(endpoint, msgId, JSON.toJSONString(payload));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}