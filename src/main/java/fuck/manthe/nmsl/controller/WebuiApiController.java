package fuck.manthe.nmsl.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v2")
public class WebuiApiController {
    @RequestMapping("ping")
    public String ping(Principal principal) {
        return "pong! Hello " + principal.getName();
    }
}
