package tw.com.blockchain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.blockchain.entity.geth.DepositEntity;
import tw.com.blockchain.repository.geth.DepositRepository;
import tw.com.blockchain.service.BaseService;
import tw.com.blockchain.service.DepositService;

/**
 * @author Kyle
 * @since 2025/3/24
 */
@Service
@CommonsLog
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DepositServiceImpl extends BaseService implements DepositService {

    private final DepositRepository depositRepository;

    @Override
    public DepositEntity save(DepositEntity depositEntity) {
        return depositRepository.save(depositEntity);
    }
}
