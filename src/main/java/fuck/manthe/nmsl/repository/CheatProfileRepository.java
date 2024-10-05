package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.CheatProfile;
import fuck.manthe.nmsl.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheatProfileRepository extends MongoRepository<CheatProfile, String> {
    Optional<CheatProfile> findAllByOwner(User owner);

    Optional<CheatProfile> findByUuid(String uuid);
}
