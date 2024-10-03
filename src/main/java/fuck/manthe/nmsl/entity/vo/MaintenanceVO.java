package fuck.manthe.nmsl.entity.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaintenanceVO {
    private boolean state;
    private long startAt;
}
