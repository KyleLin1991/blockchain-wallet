package tw.com.kyle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.kyle.entity.SystemUserEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface SystemUserRepository extends JpaRepository<SystemUserEntity, UUID> {

    Optional<SystemUserEntity> findByAccountId(UUID accountId);
}
