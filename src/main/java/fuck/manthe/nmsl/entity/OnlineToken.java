package fuck.manthe.nmsl.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Builder
@RedisHash(timeToLive = 86400)
public class OnlineToken {
    @Id
    private String id;

    @Indexed
    private String token;
    @Indexed
    private String username;
}
