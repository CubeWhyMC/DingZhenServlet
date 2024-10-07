package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.entity.*;
import fuck.manthe.nmsl.entity.dto.UpdateOnlinePreferencesDTO;
import fuck.manthe.nmsl.entity.dto.UpdatePrivateProfileDTO;
import fuck.manthe.nmsl.repository.CheatProfileRepository;
import fuck.manthe.nmsl.repository.OnlineTokenRepository;
import fuck.manthe.nmsl.service.AnalysisService;
import fuck.manthe.nmsl.service.OnlineConfigService;
import fuck.manthe.nmsl.service.UserService;
import fuck.manthe.nmsl.util.FormatUtil;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
public class OnlineConfigServiceImpl implements OnlineConfigService {
    @Resource
    OnlineTokenRepository onlineTokenRepository;

    @Resource
    CheatProfileRepository cheatProfileRepository;

    @Resource
    @Lazy
    UserService userService;

    @Resource
    AnalysisService analysisService;

    @Resource
    FormatUtil formatUtil;

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

    @Override
    public PrivateProfile savePrivateProfile(User user, PrivateProfile profile) {
        user.setPrivateProfile(profile);
        return userService.save(user).getPrivateProfile();
    }

    @Override
    public void updateCheatProfiles(String token, List<UpdatePrivateProfileDTO> updatedProfiles) {
        User user = this.findByToken(token);
        PrivateProfile privateProfile = user.getPrivateProfile();
        Map<String, String> map = new HashMap<>();
        for (UpdatePrivateProfileDTO profile : updatedProfiles) {
            Optional<CheatProfile> existProfileOptional = cheatProfileRepository.findByUuid(profile.getUuid());
            String updatedTime = formatUtil.formatVapeTime(profile.getUpdated());
            String internalId;
            if (existProfileOptional.isPresent()) {
                CheatProfile existProfile = existProfileOptional.get();
                internalId = existProfile.getId();

                existProfile.setName(profile.getName());
                existProfile.setData(profile.getData());
                existProfile.setUuid(profile.getUuid());
                existProfile.setVapeVersion(profile.getVapeVersion());
                existProfile.setLastUpdated(updatedTime);
                saveCheatProfile(existProfile);
            } else {
                CheatProfile internalProfile = saveCheatProfile(CheatProfile.builder()
                        .name(profile.getName())
                        .uuid(profile.getUuid())
                        .data(profile.getData())
                        .publicId(Math.toIntExact(cheatProfileRepository.count()))
                        .owner(user)
                        .lastUpdated(updatedTime)
                        .created(updatedTime)
                        .vapeVersion(profile.getVapeVersion())
                        .build());
                internalId = internalProfile.getId();
            }
            map.put(internalId, profile.getUuid());
        }
        privateProfile.setProfiles(map);
        savePrivateProfile(user, privateProfile);
    }

    @Override
    public CheatProfile saveCheatProfile(CheatProfile profile) {
        return cheatProfileRepository.save(profile);
    }

    @Override
    public List<CheatProfile> loadSavedProfiles(String token) {
        User user = this.findByToken(token);
        List<CheatProfile> list = new ArrayList<>();
        for (String id : user.getPrivateProfile().getProfiles().keySet()) {
            Optional<CheatProfile> profile = cheatProfileRepository.findById(id);
            profile.ifPresent(list::add);
        }
        return list;
    }

    @Override
    public CheatProfile findProfileByUuid(String uuid) {
        return cheatProfileRepository.findByUuid(uuid).orElse(null);
    }

    @Override
    public void deleteAllCheatProfile(User user) {
        cheatProfileRepository.deleteAllByOwner(user);
    }

    @Override
    public void updatePreferences(String token, UpdateOnlinePreferencesDTO dto) {
        User user = this.findByToken(token);
        PrivateProfile privateProfile = user.getPrivateProfile();
        privateProfile.setFriends(dto.getFriends());
        privateProfile.setOtherData(dto.getOtherData());
        this.savePrivateProfile(user, privateProfile);
    }

    @Override
    public CheatProfile findProfileById(String id) {
        return cheatProfileRepository.findById(id).orElse(null);
    }

    @Override
    public CheatProfile updateCheatProfile(CheatProfile profile) {
        return cheatProfileRepository.save(profile);
    }

    @Override
    public List<CheatProfile> searchProfiles(String name) {
        List<CheatProfile> list = new ArrayList<>();
        if (name.length() == 32 || name.length() == 36) {
            // is uuid
            Optional<CheatProfile> profile = cheatProfileRepository.findByUuid(name);
            profile.ifPresent(list::add);
        }
        list.addAll(cheatProfileRepository.findAllByNameContaining(name));
        return list;
    }

    @Override
    public List<String> deleteCheatProfiles(String token, List<String> queue) {
        User user = this.findByToken(token);
        List<String> deletedProfileList = new ArrayList<>();
        // todo is this internal id?
        for (String deleteProfile : queue) {
            CheatProfile profile = findProfileById(deleteProfile);
            if (Objects.equals(profile.getOwner().getId(), user.getId())) {
                deletedProfileList.add(deleteProfile);
                this.deleteCheatProfile(profile);
            }
        }
        return deletedProfileList;
    }

    @Override
    public void deleteCheatProfile(CheatProfile profile) {
        cheatProfileRepository.delete(profile);
    }
}
