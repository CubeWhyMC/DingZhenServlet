package fuck.manthe.nmsl.entity.vo;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayVO {
    private String id;

    private String name;
    private String address;
}
