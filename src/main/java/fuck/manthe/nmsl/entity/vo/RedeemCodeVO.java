package fuck.manthe.nmsl.entity.vo;

import lombok.Data;

@Data
public class RedeemCodeVO {
    private Long id;

    private String code;
    private Integer date;

    private String reseller;
    private String redeemer;
    private boolean available;
}
