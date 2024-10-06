package fuck.manthe.nmsl.entity.vo;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrivateProfileVO {
    private List<JSONObject> friends;
    private Map<String, CheatProfileVO> profiles; // internal id: uuid
    private Map<String, CheatProfileVO> publicProfiles; // internal id: uuid
    private List<JSONObject> otherData;
}
