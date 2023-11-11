package ls.EmployeeWorkOrderManagment.persistence.dao;

import ls.EmployeeWorkOrderManagment.persistence.model.token.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken, UUID> {
    Optional<ResetToken> findByToken(String token);
}
