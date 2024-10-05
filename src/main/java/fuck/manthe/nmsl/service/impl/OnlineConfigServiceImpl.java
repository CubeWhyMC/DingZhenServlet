package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.entity.*;
import fuck.manthe.nmsl.repository.OnlineTokenRepository;
import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.service.OnlineConfigService;
import fuck.manthe.nmsl.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class OnlineConfigServiceImpl implements OnlineConfigService {
    @Resource
    OnlineTokenRepository onlineTokenRepository;

    @Resource
    UserService userService;

    @Resource
    AnalysisService analysisService;

    @Override
    public void cache(String token, String username) {
        onlineTokenRepository.save(OnlineToken.builder()
                .token(token)
                .username(username)
                .build());
    }

    @Override
    public User findByToken(String token) {
        OnlineToken onlineToken = onlineTokenRepository.findByToken(token).orElse(null);
        if (onlineToken == null) return null;
        return userService.findByUsername(onlineToken.getUsername());
    }

    @Override
    public GlobalConfig loadGlobal(String token) {
        User user = this.findByToken(token);
        GlobalConfig globalConfig = user.getGlobalConfig();
        if (globalConfig == null) {
            globalConfig = GlobalConfig.builder()
                    .cache(true)
                    .firstRun(analysisService.getLastLaunch(user.getUsername()) == -1)
                    .build();
        }
        return globalConfig;
    }

    @Override
    public GlobalConfig saveGlobal(String token, GlobalConfig globalConfig) {
        User user = this.findByToken(token);
        user.setGlobalConfig(globalConfig);
        return userService.save(user).getGlobalConfig();
    }

    @Override
    public OnlineConfig loadOnline(String token) {
        User user = this.findByToken(token);
        OnlineConfig onlineConfig = user.getOnlineConfig();
        if (onlineConfig == null) {
            onlineConfig = OnlineConfig.DEFAULT; // first launch
        }
        return onlineConfig;
    }

    @Override
    public OnlineConfig saveOnline(String token, OnlineConfig onlineConfig) {
        User user = this.findByToken(token);
        user.setOnlineConfig(onlineConfig);
        return userService.save(user).getOnlineConfig();
    }

    @Override
    public List<String> loadPublicTags() {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("getvape.today");
        tags.add("Hypixel");
        tags.add("Manthe");
        tags.add("#VapeClient");
        return tags;
    }

    @Override
    public PrivateProfile loadPrivateProfile(String token) {
        User user = this.findByToken(token);
        return user.getPrivateProfile();
    }

    @Override
    public PrivateProfile savePrivateProfile(String token, PrivateProfile privateProfile) {
        User user = this.findByToken(token);
        user.setPrivateProfile(privateProfile);
        return userService.save(user).getPrivateProfile();
    }
}
