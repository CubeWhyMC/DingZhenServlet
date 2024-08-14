package fuck.manthe.nmsl.entity.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VapeAccountVO {
    String username;
    String hwid;

    long colddown;
}
