package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.Gateway;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayRepository extends MongoRepository<Gateway, String> {
}
