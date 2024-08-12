package fuck.manthe.nmsl.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("unused")
public class AnalysisDTO {
    int todayLaunch;
    long currentUsers;
    int totalLaunch;
}
