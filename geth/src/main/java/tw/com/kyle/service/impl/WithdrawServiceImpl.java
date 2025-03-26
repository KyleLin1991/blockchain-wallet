package tw.com.kyle.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.kyle.entity.geth.WithdrawEntity;
import tw.com.kyle.repository.geth.WithdrawRepository;
import tw.com.kyle.service.BaseService;
import tw.com.kyle.service.WithdrawService;

/**
 * @author Kyle
 * @since 2025/3/24
 */
@Service
@CommonsLog
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class WithdrawServiceImpl extends BaseService implements WithdrawService {

    private final WithdrawRepository withdrawRepository;

    @Override
    public WithdrawEntity save(WithdrawEntity withdrawEntity) {
        return withdrawRepository.save(withdrawEntity);
    }

}
