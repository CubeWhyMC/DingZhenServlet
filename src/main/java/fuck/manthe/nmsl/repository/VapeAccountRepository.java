package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.VapeAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VapeAccountRepository extends JpaRepository<VapeAccount, Long> {

    boolean existsByUsername(String username);
    VapeAccount findByUsername(String username);
    void deleteByUsername(String username);
}