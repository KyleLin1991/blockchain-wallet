package tw.com.blockchain.controller.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Kyle
 * @since 2025/3/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawReqDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "from address不得為空")
    private String from;

    @NotEmpty(message = "to address不得為空")
    private String to;

    @NotEmpty(message = "balance不得為空")
    private String balance;

}
