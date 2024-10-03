package fuck.manthe.nmsl.controller.admin;

import com.standardwebhooks.exceptions.WebhookSigningException;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.dto.SwitchMaintenanceDTO;
import fuck.manthe.nmsl.entity.vo.MaintenanceVO;
import fuck.manthe.nmsl.entity.webhook.MaintenanceMessage;
import fuck.manthe.nmsl.service.MaintenanceService;
import fuck.manthe.nmsl.service.WebhookService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/admin/maintenance")
public class MaintenanceController {
    @Resource
    MaintenanceService maintenanceService;

    @Resource
    WebhookService webhookService;

    @GetMapping("state")
    public RestBean<MaintenanceVO> state() {
        return RestBean.success(MaintenanceVO.builder()
                .state(maintenanceService.isMaintaining())
                .startAt(maintenanceService.getStartTime())
                .build());
    }

    @PostMapping("state")
    public RestBean<Boolean> switchState(@RequestBody SwitchMaintenanceDTO dto) throws WebhookSigningException {
        MaintenanceMessage message = new MaintenanceMessage(dto.isMaintaining());
        message.setTimestamp(System.currentTimeMillis() / 1000L);
        if (!dto.isMaintaining()) {
            message.setContent("已开启维护模式,现在只有永久用户可以注入");
            log.info("Injecting vape was limited to lifetime users only.");
        } else {
            message.setContent("已关闭维护模式,现在功能一切正常");
            log.info("Unlimited injecting.");
        }
        webhookService.pushAll("pause-inject", message); // push to webhooks

        maintenanceService.setMaintaining(dto.isMaintaining());
        return RestBean.success(dto.isMaintaining());
    }
}
