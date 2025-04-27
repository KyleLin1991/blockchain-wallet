package tw.com.blockchain.controller.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * @author Kyle
 * @since 2025/3/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRespDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String from;
    private String to;
    private String balance;
    private String transactionHash;
    private String gasPrice;
    private BigInteger nonce;
    private Timestamp withdrawTime;
}
