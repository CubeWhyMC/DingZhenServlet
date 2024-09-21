package fuck.manthe.nmsl.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseWebhookMessage {
    private String content;
    private long timestamp;
}
