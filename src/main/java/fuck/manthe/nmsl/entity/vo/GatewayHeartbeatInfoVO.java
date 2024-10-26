package fuck.manthe.nmsl.entity.vo;

import fuck.manthe.nmsl.entity.GatewayHeartbeatInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayHeartbeatInfoVO {
    private GatewayHeartbeatInfo.Status status;
    private long timestamp;
}
