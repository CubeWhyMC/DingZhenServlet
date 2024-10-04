package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.OnlineToken;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnlineTokenRepository extends KeyValueRepository<OnlineToken, String> {
    Optional<OnlineToken> findByToken(String token);

    Optional<OnlineToken> findByUsername(String username);
}
