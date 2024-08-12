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
    long todayLaunch;
    long currentUsers;
    long totalLaunch;

}
