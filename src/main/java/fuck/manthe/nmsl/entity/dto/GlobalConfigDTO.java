package fuck.manthe.nmsl.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalConfigDTO {
    @Builder.Default
    boolean cache = true;
    @Builder.Default
    boolean firstRun = false;
}
