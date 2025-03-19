package tw.com.kyle.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import tw.com.kyle.entity.geth.DepositEntity;
import tw.com.kyle.entity.geth.TransactionEntity;
import tw.com.kyle.entity.geth.WalletEntity;
import tw.com.kyle.repository.geth.DepositRepository;
import tw.com.kyle.repository.geth.TransactionRepository;
import tw.com.kyle.repository.geth.WalletRepository;

import java.math.BigInteger;
import java.util.Optional;

/**
 * @author Kyle
 * @since 2025/3/18
 */
@CommonsLog
@RequiredArgsConstructor
@Service
public class DepositListenerService {

    private final WalletRepository walletRepository;
    private final DepositRepository depositRepository;
    private final TransactionRepository transactionRepository;

    private final Web3j web3j;

    @PostConstruct
    public void init() {
        log.info("DepositListenerService initialized");
//        // 掃描指定入金區塊
//        try {
//            BigInteger targetBlock = new BigInteger(""); // 你的入金 blockNumber
//            EthBlock block = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(targetBlock), true).send();
//            if (block != null && block.getBlock() != null) {
//                log.info("Processing historical block: " + targetBlock);
//
//                Optional<WalletEntity> walletOpt = walletRepository.findByAddress("");
//                if (walletOpt.isPresent()) {
//                    WalletEntity walletEntity = walletOpt.get();
//
//                    block.getBlock().getTransactions().forEach(
//                            tx -> {
//                                EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx;
//                                String toAddress = transaction.getTo();
//                                if (walletEntity.getAddress().equalsIgnoreCase(toAddress)) {
//                                    processIncomingTransaction((EthBlock.TransactionObject) tx, walletEntity);
//                                }
//                            });
//                }
//
//            }
//        } catch (Exception e) {
//            log.error("Error processing historical block: " + e.getMessage());
//        }
        startListening();
    }

    private void startListening() {
        web3j.blockFlowable(false)
                .subscribe(block -> {
                    EthBlock ethBlock = web3j.ethGetBlockByHash(block.getBlock().getHash(), true).send();
                    log.info("block number:" + ethBlock.getBlock().getNumber());

                    ethBlock.getBlock().getTransactions().forEach(tx -> {
                        EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
                        String toAddress = transaction.getTo();
                        BigInteger balance = transaction.getValue();

                        // 檢查toAddress是否為錢包內部地址
                        Optional<WalletEntity> walletOpt = walletRepository.findByAddress(toAddress);

                        if (walletOpt.isPresent() && toAddress != null && balance.compareTo(BigInteger.ZERO) > 0) {
                            WalletEntity walletEntity = walletOpt.get();
                            processIncomingTransaction(transaction, walletEntity);
                        }
                    });
                }, error -> {
                    log.error("Error while listening: " + error.getMessage());
                    restartListening();
                });

        log.info("Started listening for incoming transactions");
    }

    public void processIncomingTransaction(EthBlock.TransactionObject transaction, WalletEntity walletEntity) {
        String toAddress = transaction.getTo();
        BigInteger balance = transaction.getValue();
        String txHash = transaction.getHash();

        try {
            TransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Transaction receipt not found for tx: " + txHash));

            BigInteger newBalance = walletEntity.getBalance().add(balance);
            walletEntity.setBalance(newBalance);

            TransactionEntity transactionEntity = TransactionEntity.builder()
                    .hash(txHash.getBytes())
                    .nonce(transaction.getNonce())
                    .from(transaction.getFrom())
                    .to(toAddress)
                    .gasPrice(transaction.getGasPrice())
                    .gas(transaction.getGas())
                    .gasUsed(receipt.getGasUsed())
                    .status(receipt.getStatus())
                    .build();

            transactionRepository.save(transactionEntity);

            depositRepository.save(DepositEntity.builder()
                    .from(transaction.getFrom())
                    .to(toAddress)
                    .balance(balance)
                    .status(receipt.getStatus())
                    .transaction(transactionEntity)
                    .build()
            );

            walletRepository.save(walletEntity);
        } catch (Exception e) {
            log.error("Error processing transaction " + txHash + ": " + e.getMessage());
        }
    }

    private void restartListening() {
        log.info("Restarting listener...");
        startListening();
    }

}
