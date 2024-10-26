package fuck.manthe.nmsl.controller.admin;

import fuck.manthe.nmsl.entity.Gateway;
import fuck.manthe.nmsl.entity.vo.GatewayVO;
import fuck.manthe.nmsl.service.GatewayService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping("admin/dashboard")
public class DashboardController {
    @Resource
    GatewayService gatewayService;

    @GetMapping("add-user")
    public String addUser() {
        return "dashboard/add-user";
    }

    @GetMapping(path = {"", "/", "index"})
    public String index() {
        return "dashboard/index";
    }

    @GetMapping("user-list")
    public String userList() {
        return "dashboard/user-list";
    }

    @GetMapping("home")
    public String home() {
        return "dashboard/home";
    }

    @GetMapping("basic-settings")
    public String basicSettings() {
        return "dashboard/basic-settings";
    }

    @GetMapping("activation")
    public String activation() {
        return "dashboard/manage-activation-codes";
    }

    @GetMapping("vape-account")
    public String vapeAccount() {
        return "dashboard/manage-vape-accounts";
    }

    @GetMapping("webhook")
    public String webhook() {
        return "dashboard/webhook";
    }

    @GetMapping("code-history")
    public String codeHistory() {
        return "dashboard/code-history";
    }

    @GetMapping("gateway")
    public String gateway() {
        return "dashboard/gateway";
    }

    @GetMapping("migrate")
    public String migrate() {
        return "dashboard/migrate";
    }

    @GetMapping("gateway/status/{id}")
    public String gatewayStatus(@PathVariable String id, Model model) {
        Gateway gateway = gatewayService.findGatewayById(id);
        model.addAttribute("gateway", gateway.asViewObject(GatewayVO.class, (vo) -> {
            vo.setAvailable(gatewayService.isAvailable(gateway));
        }));
        model.addAttribute("status", gatewayService.status(gateway));
        return "dashboard/gateway-status";
    }
}
