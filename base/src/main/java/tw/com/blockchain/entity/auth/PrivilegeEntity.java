package tw.com.blockchain.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import tw.com.blockchain.enums.EnableStatus;
import tw.com.blockchain.entity.IdEntity;

import java.util.ArrayList;
import java.util.List;

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
@Schema(description = "權限表")
@Table(name = "privilege", schema = "auth")
public class PrivilegeEntity extends IdEntity {

    @Column(name = "pid", nullable = false, length = 32)
    private String pid;

    @Column(name = "description", length = 32)
    private String description;

    @Column(name = "status", length = 1)
    private EnableStatus status;

    @OneToMany(mappedBy = "privilege")
    private List<RoleNPrivilegeEntity> roles = new ArrayList<>();;
}