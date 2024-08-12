package fuck.manthe.nmsl.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VapeAccountDTO {
    String username;
    String password;
    String hwid;
}
