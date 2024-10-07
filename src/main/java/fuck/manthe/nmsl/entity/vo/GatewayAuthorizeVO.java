package fuck.manthe.nmsl.entity.vo;

import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GatewayAuthorizeVO {
    private String token;
    private VapeAuthorizeDTO.Status status;
    private ColdDownVO coldDown;

    public enum Status {
        OK,
        SERVLET_ERROR, NO_ACCOUNT, BANNED_OR_INCORRECT
    }
}
