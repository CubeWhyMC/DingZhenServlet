package fuck.manthe.nmsl.entity.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserVO {
    private String id;
    private String username;
    private Long expire;
    private String role;

    // analysis
    private long totalLaunch;
    private long lastLaunch;
}
