package tw.com.kyle.service;

import tw.com.kyle.entity.geth.TransactionEntity;

import java.util.Optional;

/**
 * @author Kyle
 * @since 2025/3/24
 */
public interface TransactionService {

    TransactionEntity save(TransactionEntity transactionEntity);
    Optional<TransactionEntity> findByHash(byte[] hash);
}
