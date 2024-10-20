package fuck.manthe.nmsl.entity.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GatewayHeartbeatVO {
    private long time;
    private ColdDownVO coldDown;
    @Builder.Default
    private String implementation = "CubeWhyMC/DingZhenServlet"; // 服务端实现
}
