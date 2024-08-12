package fuck.manthe.nmsl.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VapeAccountInfoDTO {
    String username;
    String hwid;

    long colddown;
}
