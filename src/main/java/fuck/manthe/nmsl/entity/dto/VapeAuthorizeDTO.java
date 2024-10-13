package fuck.manthe.nmsl.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VapeAuthorizeDTO {
    private String token;
    private Status status;

    public enum Status {
        OK,
        SERVLET_ERROR, NO_ACCOUNT, INCORRECT, BANNED, CLOUDFLARE
    }
}
