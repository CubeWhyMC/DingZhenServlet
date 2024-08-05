package fuck.manthe.nmsl.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table("db_cracked")
public class CrackedUser {
    @Id(keyType = KeyType.Auto)
    private Integer id;

    private String username;
    private String password; // Vape.gg要求明文存储.
}
