package tw.com.blockchain.repository.geth;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.blockchain.entity.geth.TransactionEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/18
 */
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    Optional<TransactionEntity> findByHash(byte[] hash);
}
