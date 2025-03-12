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
 * @since 2025/3/4
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity
@Schema(description = "角色權限關聯表")
@Table(name = "role_n_privilege", schema = "auth")
public class RoleNPrivilegeEntity extends IdEntity {

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @ManyToOne
    @JoinColumn(name = "privilege_id", nullable = false)
    private PrivilegeEntity privilege;
}