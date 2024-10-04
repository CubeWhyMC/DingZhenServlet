package fuck.manthe.nmsl.entity;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PrivateProfile implements BaseData {
    private List<String> friends;
    private Map<String, CheatProfile> profiles;
    private Map<String, CheatProfile> publicProfiles;
    private List<JSONObject> otherData;
}
