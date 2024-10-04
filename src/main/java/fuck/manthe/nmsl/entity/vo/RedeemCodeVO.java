package fuck.manthe.nmsl.entity.vo;

import lombok.Data;

@Data
public class RedeemCodeVO {
    private String id;

    private String code;
    private Integer date;

    private String reseller;
    private String redeemer;
    private boolean available;
}
