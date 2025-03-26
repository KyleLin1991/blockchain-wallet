package tw.com.kyle.repository.geth;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.kyle.entity.geth.WithdrawEntity;

import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface WithdrawRepository extends JpaRepository<WithdrawEntity, UUID> {

}
