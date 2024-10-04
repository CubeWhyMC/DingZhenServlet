package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.VapeAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VapeAccountRepository extends MongoRepository<VapeAccount, String> {

    boolean existsByUsername(String username);

    VapeAccount findByUsername(String username);

    void deleteByUsername(String username);
}