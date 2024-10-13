package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.User;
import fuck.manthe.nmsl.service.MaintenanceService;
import fuck.manthe.nmsl.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class WebController {
    @Resource
    MaintenanceService maintenanceService;

    @Resource
    UserService userService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("colddown")
    public String coldDown() {
        return "colddown";
    }

    @GetMapping("queue")
    public String queue() {
        return "queue";
    }

    @Deprecated
    @GetMapping("redeem")
    public String register() {
        return "redirect:/user/redeem";
    }

    @GetMapping("user/redeem")
    public String redeem() {
        return "redeem";
    }

    @GetMapping("user/login")
    public String login() {
        return "login";
    }

    @GetMapping("maintain")
    public String maintain(@RequestParam(required = false) String redirect) {
        if (!maintenanceService.isMaintaining()) {
            if (redirect != null) {
                return "redirect:" + redirect;
            }
            return "redirect:/";
        }
        return "maintain";
    }

    @GetMapping("dashboard")
    public String dashboard(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user.getRole().equals("ADMIN")) {
            return "redirect:admin/dashboard";
        }
        return "redirect:webui/";
    }
}
