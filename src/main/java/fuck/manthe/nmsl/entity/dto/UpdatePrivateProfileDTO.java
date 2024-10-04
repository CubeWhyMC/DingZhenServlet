package fuck.manthe.nmsl.entity.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class UpdatePrivateProfileDTO {
    private JSONObject data;
    private boolean is_public;
    private String name; // profile name
    private String profileId; // internal id
    private String uuid; // public id
    private String vapeVersion;
    private long updated; // modify time
}
