package fuck.manthe.nmsl.repository;

import fuck.manthe.nmsl.entity.CrackedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<CrackedUser, Long> {
    Optional<CrackedUser> findByUsername(String username);
}
