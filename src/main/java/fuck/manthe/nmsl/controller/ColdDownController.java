package fuck.manthe.nmsl.controller;

import fuck.manthe.nmsl.entity.vo.ColdDownVO;
import fuck.manthe.nmsl.service.VapeAccountService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/colddown")
public class ColdDownController {
    @Resource
    VapeAccountService vapeAccountService;


    @GetMapping("json")
    public ColdDownVO coldDownJson() {
        return new ColdDownVO(vapeAccountService.calculateColdDown());
    }
}
