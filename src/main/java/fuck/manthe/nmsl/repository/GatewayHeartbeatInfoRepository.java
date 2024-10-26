package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.GatewayHeartbeatInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GatewayHeartbeatInfoRepository extends MongoRepository<GatewayHeartbeatInfo, String> {
    Optional<GatewayHeartbeatInfo> findByGateway(String gateway);

    List<GatewayHeartbeatInfo> findAllByGateway(String gateway);

    void deleteAllByGateway(String gateway);

    Optional<GatewayHeartbeatInfo> findFirstByGatewayOrderByCreateAtDesc(String id);
}
