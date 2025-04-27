package tw.com.blockchain.controller.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Kyle
 * @since 2025/3/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRespDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String account;
    private String roleName;
}
