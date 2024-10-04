package fuck.manthe.nmsl.entity;

import com.bol.secure.Encrypted;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class VapeAccount {
    @Id
    private String id;

    private String username;
    @Encrypted
    private String password;
    private String hwid;
}
