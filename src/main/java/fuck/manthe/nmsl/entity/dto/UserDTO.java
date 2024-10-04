package fuck.manthe.nmsl.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String id;
    private String username;
    private Long expire;

    // analysis
    private long totalLaunch;
    private long lastLaunch;
}
