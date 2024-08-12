package fuck.manthe.nmsl.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@SuppressWarnings("unused")
public class VapeAccountDTO {
    String username;
    String password;
    String hwid;
}
