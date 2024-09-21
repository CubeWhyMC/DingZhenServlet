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
public class UserRenewMessage extends BaseWebhookMessage {
    private String redeemUsername;
    private String code;
    private long expireAt;
}
