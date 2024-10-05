package fuck.manthe.nmsl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/webui")
public class WebuiController {
    @GetMapping(path = {"", "/", "index"})
    public String index() {
        return "webui/index";
    }
}
