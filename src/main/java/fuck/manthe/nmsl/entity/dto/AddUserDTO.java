package fuck.manthe.nmsl.entity.dto;

import lombok.Data;

@Data
public class AddUserDTO {
    private String username;
    private String password;
    private int days;

    private boolean admin = false;
}
