package fuck.manthe.nmsl.entity;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PrivateProfile implements BaseData {
    private List<String> friends;
    private Map<String, String> profiles; // internal id: uuid
    private Map<String, String> publicProfiles; // internal id: uuid
    private List<JSONObject> otherData;
}
