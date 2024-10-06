package fuck.manthe.nmsl.entity.vo;

import com.alibaba.fastjson2.JSONObject;
import fuck.manthe.nmsl.entity.CheatProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheatProfileVO {
    private int ownerId;

    private String uuid; // view for everyone
    private String profileId; // internal id
    private String name; // profile name
    private String vapeVersion;

    private JSONObject data;

    private String created; // created time, e.g. 2024-08-12T02:43:00.692+00:00
    private String lastUpdated; // modify time, e.g. 2024-08-13T09:51:45.259+00:00

    private Object metadata; // idk

    public static CheatProfileVO fromCheatProfile(CheatProfile profile) {
        return CheatProfileVO.builder()
                .profileId(profile.getId())
                .uuid(profile.getUuid())
                .name(profile.getName())
                .created(profile.getCreated())
                .data(profile.getData())
                .lastUpdated(profile.getLastUpdated())
                .ownerId(114514)
                .vapeVersion(profile.getVapeVersion())
                .lastUpdated(profile.getLastUpdated())
                .metadata(profile.getMetadata())
                .build();
    }
}
