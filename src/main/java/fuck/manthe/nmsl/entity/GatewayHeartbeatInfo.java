package fuck.manthe.nmsl.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class GatewayHeartbeatInfo {
    private String gateway; // gatewayId
    private Status status;

    @Indexed(expireAfter = "7d")
    private Date createAt = new Date();

    public boolean isAvailable() {
        return status == Status.OK;
    }

    public enum Status {
        OK, BAD_KEY, BAD_REQUEST, INTERNAL_ERROR, UNIMPLEMENTED_API
    }
}
