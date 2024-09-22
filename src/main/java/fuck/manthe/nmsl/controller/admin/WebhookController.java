package fuck.manthe.nmsl.controller.admin;

import com.alibaba.fastjson2.JSON;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.WebhookEndpoint;
import fuck.manthe.nmsl.entity.dto.*;
import fuck.manthe.nmsl.entity.vo.WebhookEndpointVO;
import fuck.manthe.nmsl.service.WebhookService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/webhook")
public class WebhookController {
    @Resource
    WebhookService webhookService;

    @PostMapping("add")
    public ResponseEntity<RestBean<String>> add(@RequestBody AddWebhookDTO dto) {
        webhookService.add(dto.getName(), dto.getEndpoint(), dto.getSecret());
        return ResponseEntity.ok(RestBean.success("Success"));
    }

    @DeleteMapping("{id}/remove")
    public ResponseEntity<RestBean<String>> remove(@PathVariable String id) {
        webhookService.remove(Long.parseLong(id));
        return ResponseEntity.ok(RestBean.success("Success"));
    }

    @PostMapping("{id}/name")
    public ResponseEntity<RestBean<WebhookEndpointVO>> rename(@PathVariable String id, @RequestBody RenameWebhookDTO dto) {
        WebhookEndpoint entity = webhookService.find(Long.parseLong(id));
        entity.setName(dto.getName());
        webhookService.update(entity);
        return ResponseEntity.ok(RestBean.success(entity.asViewObject(WebhookEndpointVO.class)));
    }

    @PostMapping("{id}/secret")
    public ResponseEntity<RestBean<WebhookEndpointVO>> updateSecret(@PathVariable String id, @RequestBody UpdateWebhookSecretDTO dto) {
        WebhookEndpoint entity = webhookService.find(Long.parseLong(id));
        entity.setSecret(dto.getSecret());
        webhookService.update(entity);
        return ResponseEntity.ok(RestBean.success(entity.asViewObject(WebhookEndpointVO.class)));
    }

    @PostMapping("{id}/url")
    public ResponseEntity<RestBean<WebhookEndpointVO>> updateUrl(@PathVariable String id, @RequestBody UpdateWebhookUrlDTO dto) {
        WebhookEndpoint entity = webhookService.find(Long.parseLong(id));
        entity.setUrl(dto.getUrl());
        webhookService.update(entity);
        return ResponseEntity.ok(RestBean.success(entity.asViewObject(WebhookEndpointVO.class)));
    }

    @PostMapping("{id}/test")
    public ResponseEntity<RestBean<String>> test(@PathVariable String id, @RequestBody TestWebhookDTO dto) throws Exception {
        WebhookEndpoint endpoint = webhookService.find(Long.parseLong(id));
        webhookService.push(endpoint, "test", JSON.toJSONString(dto));
        return ResponseEntity.ok(RestBean.success("Pushed"));
    }

    @GetMapping("list")
    public RestBean<List<WebhookEndpointVO>> list() {
        return RestBean.success(webhookService.list().stream().map(it -> it.asViewObject(WebhookEndpointVO.class)).toList());
    }
}
