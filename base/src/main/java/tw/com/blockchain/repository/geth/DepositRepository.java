package tw.com.blockchain.repository.geth;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.blockchain.entity.geth.DepositEntity;

import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/18
 */
public interface DepositRepository extends JpaRepository<DepositEntity, UUID> {

}
