package tw.com.blockchain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.blockchain.entity.geth.TransactionEntity;
import tw.com.blockchain.repository.geth.TransactionRepository;
import tw.com.blockchain.service.BaseService;
import tw.com.blockchain.service.TransactionService;

import java.util.Optional;

/**
 * @author Kyle
 * @since 2025/3/24
 */
@Service
@CommonsLog
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TransactionServiceImpl extends BaseService implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public TransactionEntity save(TransactionEntity transactionEntity) {
        return transactionRepository.save(transactionEntity);
    }

    @Override
    public Optional<TransactionEntity> findByHash(byte[] hash) {
        return transactionRepository.findByHash(hash);
    }
}
