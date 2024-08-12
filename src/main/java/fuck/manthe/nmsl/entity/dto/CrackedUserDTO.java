package fuck.manthe.nmsl.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrackedUserDTO {
    private Long id;
    private String username;
    private Long expire;

    // analysis
    private int totalLaunch;
}
