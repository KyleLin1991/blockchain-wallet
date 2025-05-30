package tw.com.blockchain.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import tw.com.blockchain.entity.IdEntity;
import tw.com.blockchain.enums.EnableStatus;
import tw.com.blockchain.enums.Role;
import tw.com.blockchain.enums.converter.EnableStatusConverter;
import tw.com.blockchain.enums.converter.RoleConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kyle
 * @since 2025/2/27
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity
@Schema(description = "帳號")
@Table(name = "account", schema = "auth",
        uniqueConstraints = @UniqueConstraint(columnNames = {"account", "type"}))
public class AccountEntity extends IdEntity {

    @Column(name = "account", unique = true, nullable = false, length = 128)
    private String account;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "role_code", nullable = false, length = 12)
    @Convert(converter = RoleConverter.class)
    private Role roleCode;

    @Column(name = "status", length = 1)
    @Convert(converter = EnableStatusConverter.class)
    private EnableStatus status;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private UserEntity user;

    @OneToMany(mappedBy = "account")
    private List<AccountNRoleEntity> roles = new ArrayList<>();

}
