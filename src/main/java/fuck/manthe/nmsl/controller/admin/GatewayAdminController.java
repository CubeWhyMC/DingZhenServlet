package fuck.manthe.nmsl.controller.admin;

import fuck.manthe.nmsl.entity.Gateway;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.dto.*;
import fuck.manthe.nmsl.entity.vo.GatewayVO;
import fuck.manthe.nmsl.service.GatewayService;
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

    @PutMapping("add")
    public RestBean<GatewayVO> add(@RequestBody GatewayDTO gateway) throws Exception {
        return RestBean.success(gatewayService.addGateway(Gateway.builder()
                .name(gateway.getName())
                .address(gateway.getAddress())
                .key(gateway.getKey())
                .build()).asViewObject(GatewayVO.class)
        );
    }

    @DeleteMapping("remove")
    public ResponseEntity<RestBean<String>> remove(@RequestBody RemoveGatewayDTO dto) {
        if (!gatewayService.removeGateway(dto.getId())) {
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
                        .name(gateway.getName())
                        .available(gatewayService.isAvailable(gateway))
                        .implementation(gateway.getImplementation())
                        .build()
        ).toList());
    }

    @PostMapping("{id}/name")
    public ResponseEntity<RestBean<String>> updateName(@PathVariable("id") String id, @RequestBody UpdateGatewayNameDTO dto) {
        Gateway gateway = gatewayService.findGatewayById(id);
        if (gateway == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RestBean.failure(404, "Gateway not found"));
        }
        gateway.setName(dto.getName());
        gatewayService.saveGateway(gateway);
        return ResponseEntity.ok(RestBean.success("Success"));
    }

    @PostMapping("{id}/key")
    public ResponseEntity<RestBean<String>> updateKey(@PathVariable("id") String id, @RequestBody UpdateGatewayKeyDTO dto) {
        Gateway gateway = gatewayService.findGatewayById(id);
        if (gateway == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RestBean.failure(404, "Gateway not found"));
        }
        gateway.setKey(dto.getKey());
        gatewayService.saveGateway(gateway);
        return ResponseEntity.ok(RestBean.success("Success"));
    }

    @PostMapping("{id}/address")
    public ResponseEntity<RestBean<String>> updateAddress(@PathVariable("id") String id, @RequestBody UpdateGatewayAddressDTO dto) {
        Gateway gateway = gatewayService.findGatewayById(id);
        if (gateway == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RestBean.failure(404, "Gateway not found"));
        }
        gateway.setAddress(dto.getAddress());
        gatewayService.saveGateway(gateway);
        return ResponseEntity.ok(RestBean.success("Success"));
    }

    @PostMapping("{id}/toggle")
    public ResponseEntity<RestBean<String>> toggleGateway(@PathVariable String id, @RequestBody ToggleGatewayStateDTO dto) {
        Gateway gateway = gatewayService.findGatewayById(id);
        if (gateway == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RestBean.failure(404, "Gateway not found"));
        }
        gatewayService.toggle(gateway, dto.isEnabled());
        return ResponseEntity.ok(RestBean.success("OK"));
    }
}
