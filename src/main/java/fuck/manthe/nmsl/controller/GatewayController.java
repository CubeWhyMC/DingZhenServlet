package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.VapeAccount;
import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;
import fuck.manthe.nmsl.entity.vo.GatewayAuthorizeVO;
import fuck.manthe.nmsl.service.GatewayService;
import fuck.manthe.nmsl.service.VapeAccountService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("gateway")
public class GatewayController {
    @Resource
    VapeAccountService vapeAccountService;

    @Resource
    GatewayService gatewayService;

    @GetMapping("token")
    public ResponseEntity<GatewayAuthorizeVO> token() throws Exception {
        VapeAccount account = vapeAccountService.getOne();
        VapeAuthorizeDTO dto;
        boolean hasError = false;
        if (account == null) {
            dto = VapeAuthorizeDTO.builder()
                    .token("No account")
                    .status(VapeAuthorizeDTO.Status.NO_ACCOUNT)
                    .build();
            hasError = true;
        } else {
            dto = vapeAccountService.doAuth(account);
        }

        GatewayAuthorizeVO data = gatewayService.processEncrypt(dto);
        return ResponseEntity.status((hasError) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK).body(data);
    }
}
