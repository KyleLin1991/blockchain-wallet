package tw.com.kyle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.kyle.entity.AccountEntity;
import tw.com.kyle.enums.EnableStatus;
import tw.com.kyle.enums.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByAccountAndPasswordAndStatus(String account, String password, EnableStatus status);
    List<AccountEntity> findByRoleCode(Role role);
}
