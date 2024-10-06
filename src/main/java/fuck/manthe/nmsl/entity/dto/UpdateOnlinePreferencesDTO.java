package fuck.manthe.nmsl.entity.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class UpdateOnlinePreferencesDTO {
    List<JSONObject> friends;
    List<JSONObject> otherData;
}
