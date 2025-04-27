package tw.com.blockchain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tw.com.blockchain.controller.req.WithdrawReqDto;
import tw.com.blockchain.controller.resp.WithdrawRespDto;
import tw.com.blockchain.dto.UserTokenDto;
import tw.com.blockchain.entity.geth.TransactionEntity;
import tw.com.blockchain.entity.geth.WalletEntity;
import tw.com.blockchain.entity.geth.WithdrawEntity;
import tw.com.blockchain.enums.TransactionStatus;
import tw.com.blockchain.repository.geth.WalletRepository;
import tw.com.blockchain.repository.geth.WithdrawRepository;
import tw.com.blockchain.service.BaseService;
import tw.com.blockchain.service.TransactionProcessService;
import tw.com.blockchain.service.TransactionService;
import tw.com.blockchain.service.WithdrawService;

import java.math.BigInteger;
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
public class WithdrawServiceImpl extends BaseService implements WithdrawService {

    private final WalletRepository walletRepository;
    private final WithdrawRepository withdrawRepository;
    private final TransactionService transactionService;
    private final TransactionProcessService transactionProcessService;

    @Override
    public WithdrawEntity save(WithdrawEntity withdrawEntity) {
        return withdrawRepository.save(withdrawEntity);
    }

    @Override
    public WithdrawEntity findByTransaction(byte[] transactionHash) {
        return withdrawRepository.findByTransactionHash(transactionHash);
    }

    @Override
    public WithdrawRespDto withdraw(WithdrawReqDto withdrawReqDto, Authentication authentication) {
        if (log.isDebugEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("withdrawReqDto", withdrawReqDto);

            log.debug(logParams);
        }
        UserTokenDto userTokenInfo = super.parseUserToken(authentication);

        Optional<WalletEntity> walletOpt = walletRepository.findByAddressAndCrAccount(
                withdrawReqDto.getFrom(),
                UUID.fromString(userTokenInfo.getSub())
        );

        if (walletOpt.isPresent()) {
            try {
                WalletEntity walletEntity = walletOpt.get();
                BigInteger balance = new BigInteger(withdrawReqDto.getBalance());

                if (walletEntity.getBalance().compareTo(balance) <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "錢包餘額不得小於等於提領金額");
                }
                // 發送交易
                WithdrawRespDto withdrawRespDto = transactionProcessService.sendWithdrawTransaction(
                        withdrawReqDto.getFrom(),
                        withdrawReqDto.getTo(),
                        balance
                );

                TransactionEntity transactionEntity = TransactionEntity.builder()
                        .hash(withdrawRespDto.getTransactionHash().getBytes())
                        .nonce(withdrawRespDto.getNonce())
                        .from(withdrawRespDto.getFrom())
                        .to(withdrawRespDto.getTo())
                        .gasPrice(new BigInteger(withdrawRespDto.getGasPrice()))
                        .status(TransactionStatus.PENDING)
                        .build();

                transactionService.save(transactionEntity);

                this.save(WithdrawEntity.builder()
                        .from(withdrawRespDto.getFrom())
                        .to(withdrawRespDto.getTo())
                        .balance(balance)
                        .status(TransactionStatus.PENDING)
                        .transaction(transactionEntity)
                        .build());

                return withdrawRespDto;
            } catch (Exception e) {
                log.error("Error processing withdraw from " + withdrawReqDto.getFrom() + " to " + withdrawReqDto.getTo() + ": " + e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "查無此錢包地址 " + withdrawReqDto.getFrom());
        }
    }
}
