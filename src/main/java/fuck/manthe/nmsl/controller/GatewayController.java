package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.VapeAccount;
import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;
import fuck.manthe.nmsl.service.VapeAccountService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("gateway")
public class GatewayController {
    @Resource
    VapeAccountService vapeAccountService;

    @GetMapping("token")
    public VapeAuthorizeDTO token() {
        VapeAccount account = vapeAccountService.getOne();
        if (account == null) {
            return VapeAuthorizeDTO.builder()
                    .token("No account")
                    .status(VapeAuthorizeDTO.Status.NO_ACCOUNT)
                    .build();
        }
        return vapeAccountService.doAuth(account);
    }
}
