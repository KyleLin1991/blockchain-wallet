package tw.com.kyle.service;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Numeric;
import tw.com.kyle.controller.resp.WithdrawRespDto;
import tw.com.kyle.dto.VaultDto;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * @author Kyle
 * @since 2025/3/21
 */
@Service
@CommonsLog
public class TransactionProcessService {

    @Value("${spring.vault.key-path}")
    private String vaultKeyPath;

    private final Web3j httpWeb3j;
    private final VaultTemplate vaultTemplate;

    public TransactionProcessService(
            @Qualifier("httpWeb3j") Web3j httpWeb3j,
            VaultTemplate vaultTemplate) {
        this.httpWeb3j = httpWeb3j;
        this.vaultTemplate = vaultTemplate;
    }

    public WithdrawRespDto sendWithdrawTransaction(String fromAddress, String toAddress, BigInteger amount) throws Exception {
        String privateKey = getPrivateKeyFromVault(fromAddress);
        Credentials credentials = Credentials.create(privateKey);

        BigInteger nonce = httpWeb3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST)
                .send().getTransactionCount();
        BigInteger gasPrice = httpWeb3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = estimateGasLimit(fromAddress, toAddress, amount);

        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, toAddress, amount);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction transactionResponse = httpWeb3j.ethSendRawTransaction(hexValue).send();

        log.info("Withdraw transaction sent: " + transactionResponse.getTransactionHash());
        if (transactionResponse.hasError()) {
            throw new RuntimeException("Error sending transaction: " + transactionResponse.getError().getMessage());
        }

        return WithdrawRespDto.builder()
                .from(fromAddress)
                .to(toAddress)
                .balance(amount.toString())
                .gasPrice(gasPrice.toString())
                .nonce(nonce)
                .transactionHash(transactionResponse.getTransactionHash())
                .withdrawTime(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    public BigInteger estimateGasLimit(String from, String to, BigInteger amount) throws Exception {
        Transaction transaction = Transaction.createEtherTransaction(from, null, null, null, to, amount);
        EthEstimateGas ethEstimateGas = httpWeb3j.ethEstimateGas(transaction).send();
        if (ethEstimateGas.hasError()) {
            throw new RuntimeException("Error estimating gas: " + ethEstimateGas.getError().getMessage());
        }

        return ethEstimateGas.getAmountUsed().multiply(BigInteger.TEN);
    }

    private String getPrivateKeyFromVault(String address) {
        String vaultPath = vaultKeyPath + address;
        VaultResponseSupport<VaultDto> response = vaultTemplate.read(vaultPath, VaultDto.class);
        if (response == null || response.getData() == null) {
            throw new RuntimeException("Private key not found in Vault for address: " + address);
        }

        String privateKey = response.getData().getPrivateKey();
        if (privateKey == null || privateKey.isEmpty()) {
            throw new RuntimeException("Invalid private key in Vault for address: " + address);
        }

        return privateKey;
    }

}
