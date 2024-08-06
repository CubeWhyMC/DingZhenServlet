package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @Resource
    RedisTemplate<String, Long> redisTemplate;

    @GetMapping("/")
    public String index() {
        return "index";
    }


    @GetMapping("colddown")
    public String coldDown(Model model) {
        return "colddown";
    }
}
