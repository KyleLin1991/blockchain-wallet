package tw.com.kyle.controller.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Kyle
 * @since 2025/3/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletReqDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "密碼不能為空")
    private String password;

}
