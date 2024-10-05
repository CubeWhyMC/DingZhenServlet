package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.RedeemCode;
import fuck.manthe.nmsl.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RedeemRepository extends MongoRepository<RedeemCode, String> {
    Optional<RedeemCode> findByCode(String code);

    Optional<RedeemCode> findByAvailableAndCode(boolean available, String code);

    void deleteByCode(String code);

    List<RedeemCode> findAllByAvailable(boolean state);

    void deleteAllByRedeemer(User redeemer);

    void deleteAllByRedeemerUsername(String username);
}
