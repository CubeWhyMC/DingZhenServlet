package fuck.manthe.nmsl.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizationDTO {
    String accountCreation;
    @Builder.Default
    boolean banned = false;
    @Builder.Default
    boolean licensed = true;
    @Builder.Default
    boolean profiles = true;
    @Builder.Default
    boolean registered = true;
    int userId;
    String username;
}
