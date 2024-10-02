package fuck.manthe.nmsl.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class PrivateConfigDTO {
    List<String> friends;
    Map<String, CheatProfileDTO> profiles;
    Object publicProfiles;
    List<Object> otherData;
}
