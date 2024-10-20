package fuck.manthe.nmsl.entity.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalysisVO {
    private int todayLaunch;
    private long currentUsers;
    private int todayRegister;
    private int totalLaunch;

    private boolean gateway;
    private long gatewayHeartbeat;
}
