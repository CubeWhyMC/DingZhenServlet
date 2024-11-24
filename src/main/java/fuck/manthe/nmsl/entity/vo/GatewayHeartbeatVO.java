package fuck.manthe.nmsl.entity.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GatewayHeartbeatVO {
    private long time;
    private ColdDownVO coldDown;
    private boolean authOk; // 是否可以连接到 auth.php (返回码是否为1)
    @Builder.Default
    private String implementation = "CubeWhyMC/DingZhenServlet"; // 服务端实现
}
