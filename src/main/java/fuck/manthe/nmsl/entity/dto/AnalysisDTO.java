package fuck.manthe.nmsl.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@SuppressWarnings("unused")
public class AnalysisDTO {
    long todayLaunch;
    long currentUsers;
    long todayRegister;
    long totalLaunch;
}
