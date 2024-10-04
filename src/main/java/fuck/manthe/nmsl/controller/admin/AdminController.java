package fuck.manthe.nmsl.controller.admin;

import cn.hutool.core.util.RandomUtil;
import fuck.manthe.nmsl.entity.User;
import fuck.manthe.nmsl.entity.dto.AnalysisDTO;
import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RestController
@Log4j2
public class AdminController {
    @Resource
    UserService userService;

    @Resource
    AnalysisService analysisService;

    @Value("${share.user.auto-delete-expired}")
    boolean autoDeleteExpired;

    @Resource
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userService.count() == 0) {
            String password = RandomUtil.randomString(16);
            log.warn("Admin account created");
            log.warn("Username: admin");
            log.warn("Password: {}", password);
            log.info("Dashboard at /dashboard");
            log.warn("Please keep the account information safe, you will not be able to see it again!");
            userService.addUser(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode(password))
                    .role("ADMIN")
                    .expire(-1L)
                    .build());
        }
    }

    @RequestMapping("ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Pong");
    }

    @GetMapping("analysis")
    public AnalysisDTO analysis() {
        return AnalysisDTO.builder()
                .todayLaunch(analysisService.getTodayLaunch())
                .totalLaunch(analysisService.getTotalLaunch())
                .todayRegister(analysisService.getTodayRegister())
                .currentUsers(userService.count())
                .build();
    }

    @GetMapping("logSuper")
    public String logSuper() {
        // code is in Filter
        return "Logged super-admin password in console.";
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void autoJob() {
        if (autoDeleteExpired) {
            userService.removeExpired();
        }
        analysisService.reset();
    }
}
