package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.Gateway;
import fuck.manthe.nmsl.entity.GatewayHeartbeatInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GatewayHeartbeatInfoRepository extends MongoRepository<GatewayHeartbeatInfo, String> {
    Optional<GatewayHeartbeatInfo> findByGateway(Gateway gateway);

    List<GatewayHeartbeatInfo> findAllByGateway(Gateway gateway);
}
