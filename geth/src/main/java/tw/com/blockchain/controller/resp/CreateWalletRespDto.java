package tw.com.blockchain.controller.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Kyle
 * @since 2025/3/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletRespDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String address;
    private BigDecimal balance;
}
