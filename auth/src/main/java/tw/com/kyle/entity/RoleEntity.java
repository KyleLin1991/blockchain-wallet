package tw.com.kyle.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author Kyle
 * @since 2025/3/3
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Schema(description = "角色")
@Table(name = "role", schema = "auth")
public class RoleEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Column(name = "status", length = 1)
    private String status;
}
