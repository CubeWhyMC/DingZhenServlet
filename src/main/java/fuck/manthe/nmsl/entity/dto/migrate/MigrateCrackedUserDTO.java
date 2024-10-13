package fuck.manthe.nmsl.entity.dto.migrate;

import lombok.Data;

@Data
public class MigrateCrackedUserDTO {
    private Long id;
    private String username;
    private Long expire;

    // analysis
    private long totalLaunch;
    private long lastLaunch;
}
