package fuck.manthe.nmsl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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
    @DBRef
    private User redeemer;

    private LocalDateTime createAt = LocalDateTime.now();

    private boolean available;
}
