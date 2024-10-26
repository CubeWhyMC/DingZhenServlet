package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.Gateway;
import fuck.manthe.nmsl.entity.GatewayHeartbeat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GatewayHeartbeatRepository extends MongoRepository<GatewayHeartbeat, String> {
    Optional<GatewayHeartbeat> findByGateway(Gateway gateway);
}
