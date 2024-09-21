package fuck.manthe.nmsl.entity.vo;

import lombok.Data;

@Data
public class WebhookEndpointVO {
    private long id;

    private String name;
    private String url;
}
