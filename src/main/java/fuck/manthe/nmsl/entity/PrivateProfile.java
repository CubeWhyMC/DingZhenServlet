package fuck.manthe.nmsl.entity;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrivateProfile implements BaseData {
    public static final PrivateProfile DEFAULT = PrivateProfile.builder()
            .friends(new ArrayList<>())
            .profiles(new HashMap<>())
            .publicProfiles(new HashMap<>())
            .otherData(new ArrayList<>())
            .build();

    private List<JSONObject> friends;
    private Map<String, String> profiles; // internal id: uuid
    private Map<String, String> publicProfiles; // internal id: uuid
    private List<JSONObject> otherData;
}
