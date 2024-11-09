package fuck.manthe.nmsl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;

    private String username;
    private String email;
    private boolean emailVerified = false;

    private String password;

    private String role;

    @Builder.Default
    private long expire = -1L; // 失效时间,设置为-1禁用
    @Builder.Default
    private LocalDateTime registerTime = LocalDateTime.now();

    // online
    @Builder.Default
    private OnlineConfig onlineConfig = OnlineConfig.DEFAULT;
    private GlobalConfig globalConfig;
    @Builder.Default
    private PrivateProfile privateProfile = PrivateProfile.DEFAULT;
}
