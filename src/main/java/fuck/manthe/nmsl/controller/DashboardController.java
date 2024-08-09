package fuck.manthe.nmsl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/dashboard")
@Controller
public class DashboardController {
    @GetMapping("/login")
    public String login() {
        return "dashboard/login";
    }

    @GetMapping("/add-user")
    public String addUser() {
        return "dashboard/add-user";
    }

    @GetMapping(path = {"", "/","index"})
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
}
