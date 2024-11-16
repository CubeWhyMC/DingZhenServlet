package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.User;
import fuck.manthe.nmsl.service.MaintenanceService;
import fuck.manthe.nmsl.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class WebController {
    @Resource
    MaintenanceService maintenanceService;

    @Resource
    UserService userService;

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("user/colddown")
    public String coldDown() {
        return "user/colddown";
    }

    @GetMapping("user/register")
    public String register(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "redirect:/user/renew";
        }
        return "user/register";
    }

    @GetMapping("user/renew")
    public String renew(Model model, Principal principal) {
        model.addAttribute("user", userService.findByUsername(principal.getName()));
        return "user/renew";
    }

    @GetMapping("user/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("user/forgetPassword")
    public String forgetPassword() {
        return "user/forget-password";
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
