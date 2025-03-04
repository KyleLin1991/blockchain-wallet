package tw.com.kyle.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/2/27
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "cr_user")
    private UUID crUser;

    @Schema(name = "建立時間")
    @Column(name = "cr_datetime", nullable = false)
    protected Timestamp crDatetime;

    @Column(name = "up_user")
    private UUID upUser;

    @Schema(name = "修改時間")
    @Column(name = "up_datetime")
    protected Timestamp upDatetime;

    @PrePersist
    protected void onCreate() {
        crDatetime = Timestamp.from(Instant.now());
        upDatetime = crDatetime;
    }

    @PreUpdate
    protected void onUpdate() {
        upDatetime = Timestamp.from(Instant.now());
    }
}
