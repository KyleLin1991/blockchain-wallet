package tw.com.kyle.service;

import io.reactivex.disposables.Disposable;
import jakarta.annotation.PostConstruct;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.filters.FilterException;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.websocket.WebSocketService;
import tw.com.kyle.entity.geth.DepositEntity;
import tw.com.kyle.entity.geth.TransactionEntity;
import tw.com.kyle.entity.geth.WalletEntity;
import tw.com.kyle.enums.TransactionStatus;
import tw.com.kyle.enums.converter.TransactionStatusConverter;
import tw.com.kyle.service.websocket.ReconnectListener;
import tw.com.kyle.service.websocket.ReconnectWebSocketService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Kyle
 * @since 2025/3/18
 */
@Service
@CommonsLog
public class EthereumListenerService implements ReconnectListener {

    private final Web3j httpWeb3j;
    private final Web3j wsWeb3j;
    private final WebSocketService webSocketService;
    private final DepositService depositService;
    //    private final WithdrawService withdrawService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private Disposable subscription;

    private long lastProcessedBlock = 0; // 記錄最後處理的區塊號

    public EthereumListenerService(
            @Qualifier("httpWeb3j") Web3j httpWeb3j,
            @Qualifier("wsWeb3j") Web3j wsWeb3j,
            @Qualifier("webSocketService") WebSocketService webSocketService,
//            @Qualifier("scheduler") ScheduledExecutorService scheduler,
            DepositService depositService,
            WithdrawService withdrawService,
            WalletService walletService,
            TransactionService transactionService) {
        this.httpWeb3j = httpWeb3j;
        this.wsWeb3j = wsWeb3j;
        this.webSocketService = webSocketService;
        this.depositService = depositService;
//        this.withdrawService = withdrawService;
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @PostConstruct
    public void init() {
        if (webSocketService instanceof ReconnectWebSocketService) {
            ((ReconnectWebSocketService) webSocketService).setReconnectListener(this);
        }
        startListening();
    }

    private void startListening() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose(); // 確保舊訂閱被清理
        }
        subscription = wsWeb3j.blockFlowable(false)
                .subscribe(block -> {
                    EthBlock ethBlock = wsWeb3j.ethGetBlockByHash(block.getBlock().getHash(), true).send();
                    long blockNumber = ethBlock.getBlock().getNumber().longValue();
                    lastProcessedBlock = Math.max(lastProcessedBlock, blockNumber);

                    log.info("block number: " + blockNumber);
                    processBlockTransactions(ethBlock);
                }, error -> {
                    if (error instanceof RejectedExecutionException) {
                        log.info("Subscription stopped due to scheduler shutdown: " + error.getMessage());
                    } else {
                        log.error("Error while listening: " + error.getMessage(), error);
                        if (error instanceof FilterException || error.getCause() instanceof IOException) {
                            log.info("WebSocket connection closed, triggering reconnect...");
                            if (webSocketService instanceof ReconnectWebSocketService) {
                                ((ReconnectWebSocketService) webSocketService).reconnectOnError();
                            }
                        }
                    }
                });

        log.info("Started listening for incoming transactions via WebSocket");
    }

    private void processBlockTransactions(EthBlock ethBlock) {
        // 處理區塊中的所有交易
        ethBlock.getBlock().getTransactions().forEach(tx -> {
            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
            String fromAddress = transaction.getFrom();
            String toAddress = transaction.getTo();
            BigInteger balance = transaction.getValue();

            // 檢查是否與錢包地址相關
            Optional<WalletEntity> depositWallet = walletService.findByAddressIgnoreCase(toAddress);
            Optional<WalletEntity> toWallet = walletService.findByAddressIgnoreCase(toAddress);

            // 入金：toAddress 是錢包地址且 value > 0
            if (depositWallet.isPresent() && balance.compareTo(BigInteger.ZERO) > 0) {
                WalletEntity walletEntity = depositWallet.get();
                processDepositTransaction(transaction, walletEntity);
            }

            // 出金：fromAddress 是錢包地址且 value > 0
//            if (fromWalletOpt.isPresent() && balance.compareTo(BigInteger.ZERO) > 0) {
//                processOutgoingTransaction(transaction, fromWalletOpt.get());
//            }
        });
    }

    public void processDepositTransaction(EthBlock.TransactionObject transaction, WalletEntity walletEntity) {
        String from = transaction.getFrom();
        String to = transaction.getTo();
        String txHash = transaction.getHash();
        BigInteger balance = transaction.getValue();

        if (transactionService.findByHash(txHash.getBytes()).isPresent()) {
            log.info("Deposit transaction already: " + txHash);
        } else {
            try {
                TransactionReceipt receipt = httpWeb3j.ethGetTransactionReceipt(txHash).send()
                        .getTransactionReceipt()
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Transaction receipt not found for tx: " + txHash
                        ));

                TransactionStatusConverter transactionStatusConverter = new TransactionStatusConverter();
                TransactionStatus transactionStatus = transactionStatusConverter.convertToEntityAttribute(receipt.getStatus());

                TransactionEntity transactionEntity = TransactionEntity.builder()
                        .hash(txHash.getBytes())
                        .nonce(transaction.getNonce())
                        .from(from)
                        .to(to)
                        .gasPrice(transaction.getGasPrice())
                        .gas(transaction.getGas())
                        .gasUsed(receipt.getGasUsed())
                        .status(transactionStatus)
                        .build();

                DepositEntity depositEntity = DepositEntity.builder()
                        .from(from)
                        .to(to)
                        .balance(balance)
                        .status(transactionStatus)
                        .transaction(transactionEntity)
                        .build();

                BigInteger amount = walletEntity.getBalance().add(balance);
                walletEntity.setBalance(amount);

                transactionService.save(transactionEntity);
                depositService.save(depositEntity);
                walletService.save(walletEntity);

                log.info("Deposit from " + from + " to " + to + " balance " + balance);
            } catch (Exception e) {
                log.error("Error processing deposit tx " + txHash + ": " + e.getMessage());
            }
        }
    }

    private void recoverMissedBlocks() {
        try {
            // 獲取當前最新區塊號
            EthBlockNumber latestBlock = httpWeb3j.ethBlockNumber().send();
            long currentBlock = latestBlock.getBlockNumber().longValue();

            // 從最後處理的區塊開始恢復
            for (long blockNum = lastProcessedBlock + 1; blockNum <= currentBlock; blockNum++) {
                EthBlock ethBlock = httpWeb3j.ethGetBlockByNumber(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNum)),
                        true
                ).send();

                log.info("Recovering blockNumber: " + blockNum);

                processBlockTransactions(ethBlock);
                lastProcessedBlock = blockNum;
            }
        } catch (Exception e) {
            log.error("Error recovering missed blocks: " + e.getMessage());
        }
    }

    @Override
    public void onReconnect() {
        log.info("WebSocket reconnected, restarting Ethereum Listener...");
        recoverMissedBlocks(); // 恢復遺漏區塊
        startListening(); // 重新啟動監聽
    }
}
