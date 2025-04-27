package tw.com.blockchain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Kyle
 * @since 2025/2/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String sub;
    private String account;
    private List<RoleDto> roles;
    private List<PrivilegeDto> privileges;
}
