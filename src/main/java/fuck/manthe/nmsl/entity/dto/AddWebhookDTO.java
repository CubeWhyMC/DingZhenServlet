package fuck.manthe.nmsl.entity.dto;

import lombok.Data;

@Data
public class AddWebhookDTO {
    private String name;
    private String endpoint;
    private String secret;
}
