package fuck.manthe.nmsl.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@RedisHash(timeToLive = 86400)
public class OnlineToken {
    @Id
    private String id;

    private String token;
    private String username;
}
