package tw.com.kyle.service;

import tw.com.kyle.entity.geth.WithdrawEntity;

/**
 * @author Kyle
 * @since 2025/3/24
 */
public interface WithdrawService {

    WithdrawEntity save(WithdrawEntity walletEntity);
}
