package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.CheatProfile;
import fuck.manthe.nmsl.service.OnlineConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/webui")
public class WebuiController {
    @Resource
    OnlineConfigService onlineConfigService;

    @GetMapping(path = {"", "/", "index"})
    public String index() {
        return "webui/index";
    }

    @GetMapping("edit/{uuid}")
    public String edit(Principal principal, Model model, @PathVariable String uuid) {
        // check uuid exist
        CheatProfile profile = onlineConfigService.findProfileByUuid(uuid);
        if (profile == null || !profile.getOwner().getUsername().equals(principal.getName())) {
            return "redirect:/webui?notfound";
        }
        model.addAttribute("uuid", uuid);
        return "webui/editor";
    }
}
