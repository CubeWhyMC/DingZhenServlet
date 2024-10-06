package fuck.manthe.nmsl.entity.dto;

import lombok.Data;

@Data
public class GenerateRedeemCodeDTO {
    private int count;
    private int days;
    private String reseller = "Manthe";
}
