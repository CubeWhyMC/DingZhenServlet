package fuck.manthe.nmsl.entity;

import fuck.manthe.nmsl.conventer.CryptConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VapeAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    @Convert(converter = CryptConverter.class)
    private String password;
    private String hwid;
}
