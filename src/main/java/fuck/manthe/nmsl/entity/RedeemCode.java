package fuck.manthe.nmsl.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RedeemCode implements BaseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private Integer date;

    @Builder.Default
    private String reseller = "DingZhen";
    private String redeemer;
    @Column(name = "IS_AVAILABLE")
    private boolean available;
}
