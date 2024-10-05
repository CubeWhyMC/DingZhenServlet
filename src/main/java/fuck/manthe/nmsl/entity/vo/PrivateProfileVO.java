package fuck.manthe.nmsl.entity.vo;

import com.alibaba.fastjson2.JSONObject;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class PrivateProfileVO {
    private List<String> friends;
    private Map<String, CheatProfileVO> profiles; // internal id: uuid
    private Map<String, CheatProfileVO> publicProfiles; // internal id: uuid
    private List<JSONObject> otherData;
}
