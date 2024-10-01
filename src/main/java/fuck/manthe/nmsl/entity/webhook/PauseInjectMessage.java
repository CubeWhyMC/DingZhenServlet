package fuck.manthe.nmsl.entity.webhook;

import fuck.manthe.nmsl.entity.BaseWebhookMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PauseInjectMessage extends BaseWebhookMessage {
    private boolean state;
}
