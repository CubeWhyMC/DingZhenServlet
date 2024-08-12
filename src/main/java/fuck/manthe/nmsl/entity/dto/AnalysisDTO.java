package fuck.manthe.nmsl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("unused")
public class Analysis {
    long todayLaunch;
    long currentUsers;
    long totalLaunch;
}
