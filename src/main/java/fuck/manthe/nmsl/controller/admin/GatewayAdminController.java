package fuck.manthe.nmsl.controller.admin;

import fuck.manthe.nmsl.entity.Gateway;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.dto.GatewayDTO;
import fuck.manthe.nmsl.entity.vo.GatewayVO;
import fuck.manthe.nmsl.service.GatewayService;
import fuck.manthe.nmsl.utils.CryptUtil;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/gateway")
public class GatewayAdminController {
    @Resource
    GatewayService gatewayService;
    @Resource
    CryptUtil cryptUtil;

    @PutMapping("add")
    public RestBean<String> add(@RequestBody GatewayDTO gateway) throws Exception {
        gatewayService.addGateway(Gateway.builder()
                        .name(gateway.getName())
                .address(gateway.getAddress())
                .key(gateway.getKey())
                .build());
        return RestBean.success("Success");
    }

    @DeleteMapping("remove")
    public ResponseEntity<RestBean<String>> remove(@RequestParam long id) throws Exception {
        if (gatewayService.removeGateway(id)) {
            return new ResponseEntity<>(RestBean.failure(404, "Gateway not found"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(RestBean.success("Success"));
    }

    @GetMapping("list")
    public RestBean<List<GatewayVO>> list() throws Exception {
        return RestBean.success(gatewayService.list().stream().map(
                gateway -> GatewayVO.builder()
                        .address(gateway.getAddress())
                        .id(gateway.getId())
                        .name(gateway.getName()).build()
        ).toList());
    }
}
