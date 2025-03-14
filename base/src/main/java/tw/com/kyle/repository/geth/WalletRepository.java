package tw.com.kyle.repository.geth;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.kyle.entity.geth.WalletEntity;

import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {

}
