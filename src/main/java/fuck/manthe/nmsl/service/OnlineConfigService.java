package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.*;
import fuck.manthe.nmsl.entity.dto.UpdatePrivateProfileDTO;

import java.util.List;
import java.util.Map;

public interface OnlineConfigService {
    void cache(String token, String username);

    User findByToken(String token);

    GlobalConfig loadGlobal(String token);

    GlobalConfig saveGlobal(String token, GlobalConfig globalConfig);

    OnlineConfig loadOnline(String token);

    OnlineConfig saveOnline(String token, OnlineConfig onlineConfig);

    List<String> loadPublicTags();

    PrivateProfile loadPrivateProfile(String token);

    PrivateProfile savePrivateProfile(String token, PrivateProfile privateProfile);

    void updateCheatProfiles(String token, List<UpdatePrivateProfileDTO> updatedProfile);

    CheatProfile saveCheatProfile(CheatProfile profile);

    List<CheatProfile> loadSavedProfiles(String token);

    CheatProfile findProfileByUuid(String uuid);
}
