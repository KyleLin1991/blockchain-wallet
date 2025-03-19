package tw.com.kyle.repository.geth;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.kyle.entity.geth.DepositEntity;

import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/18
 */
public interface DepositRepository extends JpaRepository<DepositEntity, UUID> {

}
