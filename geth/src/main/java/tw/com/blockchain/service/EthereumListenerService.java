package tw.com.blockchain.service;

import io.reactivex.disposables.Disposable;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import tw.com.blockchain.entity.geth.DepositEntity;
import tw.com.blockchain.entity.geth.TransactionEntity;
import tw.com.blockchain.entity.geth.WalletEntity;
import tw.com.blockchain.entity.geth.WithdrawEntity;
import tw.com.blockchain.enums.TransactionStatus;
import tw.com.blockchain.enums.converter.TransactionStatusConverter;
import tw.com.blockchain.service.websocket.ReconnectListener;
import tw.com.blockchain.service.websocket.ReconnectWebSocketService;
import tw.com.blockchain.service.websocket.WebSocketConnectedEvent;

import java.math.BigInteger;
import java.util.Optional;

/**
 * @author Kyle
 * @since 2025/3/18
 */
@Service
@CommonsLog
public class EthereumListenerService implements ReconnectListener {

    private final Web3j httpWeb3j;
    private final Web3j wsWeb3j;
    private final DepositService depositService;
    private final WithdrawService withdrawService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private Disposable subscription;

    private long lastProcessedBlock = 0; // 記錄最後處理的區塊號

    @Autowired
    private ReconnectWebSocketService reconnectWebSocketService;

    public EthereumListenerService(
            @Qualifier("httpWeb3j") Web3j httpWeb3j,
            @Qualifier("wsWeb3j") Web3j wsWeb3j,
            DepositService depositService,
            WithdrawService withdrawService,
            WalletService walletService,
            TransactionService transactionService) {
        this.httpWeb3j = httpWeb3j;
        this.wsWeb3j = wsWeb3j;
        this.depositService = depositService;
        this.withdrawService = withdrawService;
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @EventListener
    public void onWebSocketConnected(WebSocketConnectedEvent event) {
        startListening();
    }

    private void startListening() {
        if (!reconnectWebSocketService.isConnected()) {
            log.warn("WebSocket not connected yet, skipping startListening");
            return;
        }
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose(); // 確保舊訂閱被清理
        }
        reconnectWebSocketService.setReconnectListener(this);

        subscription = wsWeb3j.blockFlowable(false)
                .subscribe(block -> {
                    EthBlock ethBlock = wsWeb3j.ethGetBlockByHash(block.getBlock().getHash(), true).send();
                    long blockNumber = ethBlock.getBlock().getNumber().longValue();
                    lastProcessedBlock = Math.max(lastProcessedBlock, blockNumber);

                    log.info("block number: " + blockNumber);
                    processBlockTransactions(ethBlock);
                }, error -> log.error("Error while listening: " + error.getMessage(), error));

        log.info("Started listening for incoming transactions via WebSocket");
    }

    private void processBlockTransactions(EthBlock ethBlock) {
        // 處理區塊中的所有交易
        ethBlock.getBlock().getTransactions().forEach(tx -> {
            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
            String fromAddress = transaction.getFrom();
            String toAddress = transaction.getTo();

            // 交易金額
            BigInteger balance = transaction.getValue();

            // 檢查是否與錢包地址相關
            Optional<WalletEntity> depositWallet = walletService.findByAddressIgnoreCase(toAddress);
            Optional<WalletEntity> withdrawWallet = walletService.findByAddressIgnoreCase(fromAddress);

            // 入金：toAddress 是錢包地址且 value > 0
            if (depositWallet.isPresent() && balance.compareTo(BigInteger.ZERO) > 0) {
                WalletEntity walletEntity = depositWallet.get();
                processDepositTransaction(transaction, walletEntity);
            }

            // 出金：fromAddress 是錢包地址且 value > 0
            if (withdrawWallet.isPresent() && balance.compareTo(BigInteger.ZERO) > 0) {
                processWithdrawTransaction(transaction, withdrawWallet.get());
            }
        });
    }

    public void processDepositTransaction(EthBlock.TransactionObject transaction, WalletEntity walletEntity) {
        String from = transaction.getFrom();
        String to = transaction.getTo();
        String txHash = transaction.getHash();
        BigInteger balance = transaction.getValue();

        httpWeb3j.ethGetTransactionReceipt(txHash)
                .sendAsync()
                .thenAccept(response -> {
                    try {
                        TransactionReceipt receipt = response.getTransactionReceipt()
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
                })
                .exceptionally(throwable -> {
                    log.error("Failed to fetch receipt for tx " + txHash + ": " + throwable.getMessage());
                    return null;
                });
    }

    public void processWithdrawTransaction(EthBlock.TransactionObject transaction, WalletEntity walletEntity) {
        String from = transaction.getFrom();
        String to = transaction.getTo();
        String txHash = transaction.getHash();
        BigInteger balance = transaction.getValue();

        httpWeb3j.ethGetTransactionReceipt(txHash)
                .sendAsync()
                .thenAccept(response -> {
                    try {
                        TransactionReceipt receipt = response.getTransactionReceipt()
                                .orElseThrow(() -> new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Transaction receipt not found for tx: " + txHash
                                ));
                        TransactionStatusConverter transactionStatusConverter = new TransactionStatusConverter();
                        TransactionStatus transactionStatus = transactionStatusConverter.convertToEntityAttribute(receipt.getStatus());

                        TransactionEntity transactionEntity = transactionService.findByHash(txHash.getBytes())
                                .orElseThrow(() -> new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Transaction not found for tx: " + txHash
                                ));
                        transactionEntity.setGasPrice(transaction.getGasPrice());
                        transactionEntity.setGas(transaction.getGas());
                        transactionEntity.setGasUsed(receipt.getGasUsed());
                        transactionEntity.setStatus(transactionStatus);

                        WithdrawEntity withdrawEntity = withdrawService.findByTransaction(txHash.getBytes());
                        withdrawEntity.setStatus(transactionStatus);

                        walletEntity.setBalance(new BigInteger(walletService.getBalance(walletEntity.getAddress())));

                        transactionService.save(transactionEntity);
                        withdrawService.save(withdrawEntity);
                        walletService.save(walletEntity);

                        log.info("Withdraw from " + from + " to " + to + " balance " + balance);
                    } catch (Exception e) {
                        log.error("Error processing Withdraw tx " + txHash + ": " + e.getMessage());
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Failed to fetch receipt for tx " + txHash + ": " + throwable.getMessage());
                    return null;
                });
    }

    private void recoverMissedBlocks() {
        try {
            // 獲取當前最新區塊號
            EthBlockNumber latestBlock = wsWeb3j.ethBlockNumber().send();
            long currentBlock = latestBlock.getBlockNumber().longValue();

            // 從最後處理的區塊開始恢復
            for (long blockNum = lastProcessedBlock + 1; blockNum < currentBlock; blockNum++) {
                EthBlock ethBlock = wsWeb3j.ethGetBlockByNumber(
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
    }
}
