package tw.com.kyle.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import tw.com.kyle.enums.EnableStatus;
import tw.com.kyle.enums.Role;
import tw.com.kyle.enums.UserType;

/**
 * @author Kyle
 * @since 2025/2/27
 */
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@SuperBuilder
@Entity
@Schema(description = "帳號")
@Table(name = "account", schema = "auth")
public class AccountEntity extends BaseEntity {

    @Column(name = "account", unique = true, nullable = false, length = 128)
    private String account;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "type", nullable = false, length = 1)
    private UserType type;

    @Column(name = "status", length = 1)
    private EnableStatus enableStatus;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private UserEntity user;

}
