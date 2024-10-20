package fuck.manthe.nmsl.entity.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserVO {
    private String id;
    private String username;
    private String email;
    private String role;
    private long expire;
    private long registerTime;

    // analysis
    private long totalLaunch;
    private long lastLaunch;
}
