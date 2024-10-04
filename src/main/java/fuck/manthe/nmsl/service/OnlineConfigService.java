package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.GlobalConfig;
import fuck.manthe.nmsl.entity.OnlineConfig;
import fuck.manthe.nmsl.entity.User;

import java.util.List;

public interface OnlineConfigService {
    void cache(String token, String username);

    User findByToken(String token);

    GlobalConfig loadGlobal(String token);

    GlobalConfig saveGlobal(String token, GlobalConfig globalConfig);

    OnlineConfig loadOnline(String token);

    OnlineConfig saveOnline(String token, OnlineConfig onlineConfig);

    List<String> loadPublicTags();
}
