package fuck.manthe.nmsl.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayVO {
    private String id;
    private boolean enabled;

    private String name;
    private String address;
    private String implementation;
    private boolean available;
}
