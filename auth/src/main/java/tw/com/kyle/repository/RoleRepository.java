package tw.com.kyle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.kyle.entity.RoleEntity;
import tw.com.kyle.enums.Role;

import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    RoleEntity findByRoleCode(Role roleCode);
}
