package tw.com.blockchain.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import tw.com.blockchain.enums.EnableStatus;
import tw.com.blockchain.enums.Role;
import tw.com.blockchain.enums.converter.RoleConverter;
import tw.com.blockchain.entity.IdEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kyle
 * @since 2025/3/3
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity
@Schema(description = "角色")
@Table(name = "role", schema = "auth")
public class RoleEntity extends IdEntity {

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Column(name = "role_code", nullable = false, length = 12)
    @Convert(converter = RoleConverter.class)
    private Role roleCode;

    @Column(name = "status", length = 1)
    private EnableStatus status;

    @OneToMany(mappedBy = "role")
    private List<AccountNRoleEntity> accountNRoles = new ArrayList<>();

    @OneToMany(mappedBy = "role")
    private List<RoleNPrivilegeEntity> roleNPrivileges = new ArrayList<>();
}
