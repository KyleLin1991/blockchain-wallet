package tw.com.kyle.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.kyle.entity.geth.DepositEntity;
import tw.com.kyle.repository.geth.DepositRepository;
import tw.com.kyle.service.BaseService;
import tw.com.kyle.service.DepositService;

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
