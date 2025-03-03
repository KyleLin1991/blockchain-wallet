package tw.com.kyle.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tw.com.kyle.enums.Role;

/**
 * @author Kyle
 * @since 2025/2/27
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Schema(description = "帳號")
@Table(name = "account", schema = "auth")
public class AccountEntity extends BaseEntity {

    @Column(name = "account", nullable = false, length = 128)
    private String account;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "role_code", length = 1)
    private Role roleCode;
}
