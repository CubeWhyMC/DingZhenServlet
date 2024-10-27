package fuck.manthe.nmsl.entity.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String email = null;
    private String code;
}
