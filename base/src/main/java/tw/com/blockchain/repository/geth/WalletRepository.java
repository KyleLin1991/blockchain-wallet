package tw.com.blockchain.repository.geth;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.blockchain.entity.geth.WalletEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {

    Optional<WalletEntity> findByAddressIgnoreCase(String address);
    Optional<WalletEntity> findByAddressAndCrAccount(String address, UUID account);
}
