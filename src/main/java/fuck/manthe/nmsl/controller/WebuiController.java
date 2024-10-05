package fuck.manthe.nmsl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/webui")
public class WebuiController {
    @GetMapping(path = {"", "/", "index"})
    public String index() {
        return "webui/index";
    }

    @GetMapping("edit/{uuid}")
    public String edit(Model model, @PathVariable String uuid) {
        model.addAttribute("uuid", uuid);
        return "webui/editor";
    }
}
