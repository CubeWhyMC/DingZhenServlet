package fuck.manthe.nmsl.entity.dto;

import lombok.Data;

@Data
public class ForgetPasswordDTO {
    private String username;
    private String redeemCode;

    private String password; // new password
}
