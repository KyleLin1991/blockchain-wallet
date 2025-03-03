package tw.com.kyle.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tw.com.kyle.enums.UserStatus;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/2/27
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Schema(description = "後台使用者")
@Table(name = "system_user", schema = "auth")
public class SystemUserEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Column(name = "email", unique = true, nullable = false, length = 128)
    private String email;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Column(name = "status", length = 1)
    private UserStatus userStatus;

    @Column(name = "up_user")
    private UUID upUser;

    @Column(name = "up_datetime")
    private Timestamp upDatetime;
}
