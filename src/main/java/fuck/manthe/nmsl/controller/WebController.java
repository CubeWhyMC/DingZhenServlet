package fuck.manthe.nmsl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
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
}
