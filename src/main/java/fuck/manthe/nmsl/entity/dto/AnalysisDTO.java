package fuck.manthe.nmsl.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@SuppressWarnings("unused")
public class AnalysisDTO {
    int todayLaunch;
    long currentUsers;
    int todayRegister;
    int totalLaunch;
}
