package fuck.manthe.nmsl.controller.admin;

import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.dto.SwitchMaintenanceDTO;
import fuck.manthe.nmsl.entity.vo.MaintenanceVO;
import fuck.manthe.nmsl.service.MaintenanceService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/admin/maintenance")
public class MaintenanceController {
    @Resource
    MaintenanceService maintenanceService;

    @GetMapping("state")
    public RestBean<MaintenanceVO> state() {
        return RestBean.success(MaintenanceVO.builder()
                .state(maintenanceService.isMaintaining())
                .startAt(maintenanceService.getStartTime())
                .build());
    }

    @PostMapping("state")
    public RestBean<Boolean> switchState(@RequestBody SwitchMaintenanceDTO dto) {
        maintenanceService.setMaintaining(dto.isMaintaining());
        return RestBean.success(dto.isMaintaining());
    }
}
