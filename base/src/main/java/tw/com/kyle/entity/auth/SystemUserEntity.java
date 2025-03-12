package tw.com.kyle.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import tw.com.kyle.enums.EnableStatus;
import tw.com.kyle.entity.BaseTimeEntity;

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
@Schema(description = "後台使用者")
@Table(name = "system_user", schema = "auth")
public class SystemUserEntity extends BaseTimeEntity {

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Column(name = "email", unique = true, nullable = false, length = 128)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "address", length = 128)
    private String address;

    @Column(name = "status", length = 1)
    private EnableStatus enableStatus;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountEntity account;
}
