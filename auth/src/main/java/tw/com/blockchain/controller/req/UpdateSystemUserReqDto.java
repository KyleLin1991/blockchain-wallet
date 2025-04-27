package tw.com.blockchain.controller.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSystemUserReqDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "userId不得為空")
    private UUID userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
}
