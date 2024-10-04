package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.service.MaintenanceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    @Resource
    MaintenanceService maintenanceService;

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

    @GetMapping("redeem")
    public String register() {
        return "redeem";
    }

    @GetMapping("login")
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
}
