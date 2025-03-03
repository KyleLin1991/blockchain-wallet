package tw.com.kyle.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

/**
 * @author Kyle
 * @since 2025/3/3
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Schema(description = "使用者角色關聯表")
@Table(name = "user_n_role", schema = "auth")
public class UserNRoleEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private BigInteger userId;

    @Column(name = "role_id", nullable = false)
    private BigInteger roleId;
}
