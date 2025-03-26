package tw.com.kyle.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;
import tw.com.kyle.controller.req.CreateWalletReqDto;
import tw.com.kyle.controller.req.WithdrawReqDto;
import tw.com.kyle.controller.resp.CreateWalletRespDto;
import tw.com.kyle.controller.resp.WithdrawRespDto;
import tw.com.kyle.dto.UserTokenDto;
import tw.com.kyle.dto.VaultDto;
import tw.com.kyle.entity.auth.AccountEntity;
import tw.com.kyle.entity.geth.TransactionEntity;
import tw.com.kyle.entity.geth.WalletEntity;
import tw.com.kyle.entity.geth.WithdrawEntity;
import tw.com.kyle.enums.TransactionStatus;
import tw.com.kyle.enums.converter.TransactionStatusConverter;
import tw.com.kyle.misc.GethProperties;
import tw.com.kyle.repository.auth.AccountRepository;
import tw.com.kyle.repository.geth.TransactionRepository;
import tw.com.kyle.repository.geth.WalletRepository;
import tw.com.kyle.repository.geth.WithdrawRepository;
import tw.com.kyle.service.BaseService;
import tw.com.kyle.service.TransactionProcessService;
import tw.com.kyle.service.TransactionService;
import tw.com.kyle.service.WalletService;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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
