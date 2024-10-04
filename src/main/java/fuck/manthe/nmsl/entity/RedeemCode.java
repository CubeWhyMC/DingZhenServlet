package fuck.manthe.nmsl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
public class RedeemCode implements BaseData {
    @Id
    private String id;

    private String code;
    private Integer date;

    @Builder.Default
    private String reseller = "Manthe";
    private String redeemer;

    private boolean available;
}
