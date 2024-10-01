package fuck.manthe.nmsl.entity.dto;

import lombok.Data;

@Data
public class VerifyLoginDTO {
    private String username;
    private String hashedPassword; // password with sha1
}
