package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.WebhookEndpoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookEndpointRepository extends MongoRepository<WebhookEndpoint, String> {
    void deleteByName(String name);
}
