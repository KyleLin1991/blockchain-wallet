package tw.com.kyle.controller.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.kyle.enums.Role;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Kyle
 * @since 2025/3/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginReqDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "帳號不能為空")
    private String account;

    @NotEmpty(message = "密碼不能為空")
    private String password;

    @NotNull(message = "角色不能為空")
    private Role role;
}
