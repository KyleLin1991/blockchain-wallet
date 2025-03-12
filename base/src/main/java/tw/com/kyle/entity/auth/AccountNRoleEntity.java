package tw.com.kyle.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import tw.com.kyle.entity.IdEntity;

/**
 * @author Kyle
 * @since 2025/3/3
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@SuperBuilder
@Entity
@Schema(description = "帳號角色關聯表")
@Table(name = "account_n_role", schema = "auth")
public class AccountNRoleEntity extends IdEntity {

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;
}
