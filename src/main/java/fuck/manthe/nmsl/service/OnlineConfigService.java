package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.*;
import fuck.manthe.nmsl.entity.dto.UpdateOnlinePreferencesDTO;
import fuck.manthe.nmsl.entity.dto.UpdatePrivateProfileDTO;

import java.util.List;

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

    PrivateProfile savePrivateProfile(User user, PrivateProfile profile);

    void updateCheatProfiles(String token, List<UpdatePrivateProfileDTO> updatedProfile);

    CheatProfile saveCheatProfile(CheatProfile profile);

    List<CheatProfile> loadSavedProfiles(String token);

    CheatProfile findProfileByUuid(String uuid);

    void deleteAllCheatProfile(User user);

    void updatePreferences(String token, UpdateOnlinePreferencesDTO dto);

    CheatProfile findProfileById(String id);

    CheatProfile updateCheatProfile(CheatProfile profile);

    List<CheatProfile> searchProfiles(String name);
}
