package tw.com.blockchain.repository.geth;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.blockchain.entity.geth.WithdrawEntity;

import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface WithdrawRepository extends JpaRepository<WithdrawEntity, UUID> {

    WithdrawEntity findByTransactionHash(byte[] transactionId);
}
