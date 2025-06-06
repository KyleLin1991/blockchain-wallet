package tw.com.blockchain.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.blockchain.entity.auth.AccountNRoleEntity;

import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface AccountNRoleRepository extends JpaRepository<AccountNRoleEntity, UUID> {

}
