package fuck.manthe.nmsl.entity.dto.migrate;

import lombok.Data;

@Data
public class MigrateRedeemCodeDTO {
    private Long id;

    private String code;
    private Integer date;

    private String reseller;
    private String redeemer;
    private boolean available;
}
