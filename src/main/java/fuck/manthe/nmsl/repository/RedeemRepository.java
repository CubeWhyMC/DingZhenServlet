package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.RedeemCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedeemRepository extends JpaRepository<RedeemCode, Long> {
    Optional<RedeemCode> findByCode(String code);
    void deleteByCode(String code);
}