package tw.com.blockchain.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.blockchain.entity.auth.RoleEntity;
import tw.com.blockchain.enums.Role;

import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    RoleEntity findByRoleCode(Role roleCode);
}
