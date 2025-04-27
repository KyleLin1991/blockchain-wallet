package tw.com.blockchain.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.blockchain.entity.auth.UserEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByAccountId(UUID accountId);
}
