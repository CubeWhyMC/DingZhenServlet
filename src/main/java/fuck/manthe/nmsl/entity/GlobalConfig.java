package fuck.manthe.nmsl.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalConfig implements BaseData {
    private boolean cache;
    private boolean firstRun;
}
