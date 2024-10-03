package fuck.manthe.nmsl.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping("dashboard")
public class DashboardController {
    @GetMapping("login")
    public String login() {
        return "dashboard/login";
    }

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
}
