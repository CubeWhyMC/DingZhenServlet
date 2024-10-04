package fuck.manthe.nmsl.entity.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalConfigVO {
    private boolean cache;
    private boolean firstRun;
}
