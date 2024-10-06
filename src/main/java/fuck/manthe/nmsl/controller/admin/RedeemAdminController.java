package fuck.manthe.nmsl.controller.admin;

import fuck.manthe.nmsl.entity.RedeemCode;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.dto.DestroyRedeemCodeDTO;
import fuck.manthe.nmsl.entity.dto.GenerateRedeemCodeDTO;
import fuck.manthe.nmsl.entity.vo.RedeemCodeVO;
import fuck.manthe.nmsl.service.RedeemService;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/redeem")
public class RedeemAdminController {
    @Resource
    RedeemService redeemService;

    @GetMapping("list")
    public ResponseEntity<RestBean<List<RedeemCodeVO>>> redeemCodeList() {
        return ResponseEntity.ok(RestBean.success(redeemService.list().stream().map(code -> code.asViewObject(RedeemCodeVO.class)).toList()));
    }

    @GetMapping("available")
    public ResponseEntity<RestBean<List<RedeemCodeVO>>> availableRedeemCode() {
        return ResponseEntity.ok(RestBean.success(redeemService.listAvailable().stream().map(code -> code.asViewObject(RedeemCodeVO.class)).toList()));
    }

    @GetMapping("sold")
    public ResponseEntity<RestBean<List<RedeemCodeVO>>> listSold() {
        return ResponseEntity.ok(RestBean.success(redeemService.listSold().stream().map(code -> code.asViewObject(RedeemCodeVO.class)).toList()));
    }

    @DeleteMapping("destroy")
    public ResponseEntity<RestBean<String>> destroyRedeemCode(@RequestBody DestroyRedeemCodeDTO dto) {
        if (redeemService.removeCode(dto.getCode())) {
            return ResponseEntity.ok(RestBean.success("Success"));
        }
        return new ResponseEntity<>(RestBean.failure(404, "Code not found"), HttpStatus.NOT_FOUND);
    }

    @PostMapping("generate")
    public ResponseEntity<RestBean<List<RedeemCode>>> generateRedeemCode(@RequestBody GenerateRedeemCodeDTO dto) {
        List<RedeemCode> result = new ArrayList<>();
        for (int i = 0; i < dto.getCount(); i++) {
            RedeemCode code = generateOne(dto.getDays(), dto.getReseller());
            redeemService.addCode(code);
            result.add(code);
        }
        return ResponseEntity.ok(RestBean.success(result));
    }

    @NotNull
    private RedeemCode generateOne(int day, String reseller) {
        RedeemCode redeemCode = new RedeemCode();
        redeemCode.setDate(day);
        redeemCode.setReseller((reseller != null) ? reseller : "Manthe");
        redeemCode.setAvailable(true);
        redeemCode.setCode(UUID.randomUUID().toString());
        return redeemCode;
    }
}
