package tw.com.kyle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.kyle.entity.AccountNRoleEntity;

import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface AccountNRoleRepository extends JpaRepository<AccountNRoleEntity, UUID> {

}
