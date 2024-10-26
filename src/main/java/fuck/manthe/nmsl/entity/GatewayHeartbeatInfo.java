package fuck.manthe.nmsl.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("gateway_heartbeat")
public class GatewayHeartbeatInfo {
    @DBRef
    private Gateway gateway;
    private Status status;

    @Indexed(expireAfter = "7d")
    private LocalDateTime timestamp = LocalDateTime.now();

    public enum Status {
        OK, BAD_KEY, BAD_REQUEST, UNIMPLEMENTED_API
    }

    public boolean isAvailable() {
        return status == Status.OK;
    }
}
