package fuck.manthe.nmsl.controller.admin;

import fuck.manthe.nmsl.entity.dto.AnalysisDTO;
import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.service.CrackedUserService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RestController
@Log4j2
public class AdminController {
    @Resource
    CrackedUserService crackedUserService;

    @Resource
    AnalysisService analysisService;

    @Value("${share.user.auto-delete-expired}")
    boolean autoDeleteExpired;

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
                .currentUsers(crackedUserService.count())
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
            crackedUserService.removeExpired();
        }
        analysisService.reset();
    }
}
