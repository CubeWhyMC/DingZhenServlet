package fuck.manthe.nmsl.entity.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnerVO {
    private int userId;
    private String username;
}
