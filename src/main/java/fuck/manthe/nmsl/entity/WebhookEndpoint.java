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
@AllArgsConstructor
@NoArgsConstructor
public class WebhookEndpoint implements BaseData {
    @Id
    private String id;

    private String name;
    private String url;

    @Encrypted
    private String secret;
}
