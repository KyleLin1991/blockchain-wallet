package tw.com.blockchain.service;

import tw.com.blockchain.entity.geth.DepositEntity;

/**
 * @author Kyle
 * @since 2025/3/24
 */
public interface DepositService {

    DepositEntity save(DepositEntity depositEntity);
}
