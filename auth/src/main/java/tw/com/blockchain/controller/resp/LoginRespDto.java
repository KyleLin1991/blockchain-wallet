package tw.com.blockchain.controller.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.blockchain.dto.PrivilegeDto;
import tw.com.blockchain.dto.RoleDto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Kyle
 * @since 2025/3/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRespDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String account;
    private String token;
    private List<RoleDto> roles;
    private List<PrivilegeDto> privileges;
}
