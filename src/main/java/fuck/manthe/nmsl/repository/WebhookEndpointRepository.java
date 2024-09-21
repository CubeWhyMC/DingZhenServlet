package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.WebhookEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookEndpointRepository extends JpaRepository<WebhookEndpoint, Long> {
    void deleteByName(String name);
}
