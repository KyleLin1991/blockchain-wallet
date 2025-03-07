package tw.com.kyle.controller.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.kyle.dto.PrivilegeDto;
import tw.com.kyle.dto.RoleDto;

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
public class RegisterRespDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String account;
    private String roleName;
}
