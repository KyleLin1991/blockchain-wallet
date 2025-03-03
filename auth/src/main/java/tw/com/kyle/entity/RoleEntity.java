package tw.com.kyle.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import tw.com.kyle.enums.EnableStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kyle
 * @since 2025/3/3
 */
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@SuperBuilder
@Entity
@Schema(description = "角色")
@Table(name = "role", schema = "auth")
public class RoleEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Column(name = "status", length = 1)
    private EnableStatus status;

    @OneToMany(mappedBy = "role")
    private List<UserNRoleEntity> users = new ArrayList<>();;

    @OneToMany(mappedBy = "role")
    private List<RoleNPrivilegeEntity> privileges = new ArrayList<>();;
}
