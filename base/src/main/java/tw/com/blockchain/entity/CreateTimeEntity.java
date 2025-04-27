package tw.com.blockchain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
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
public class CreateTimeEntity extends IdEntity {

    @Column(name = "cr_account")
    private UUID crAccount;

    @Schema(name = "建立時間")
    @Column(name = "cr_datetime", nullable = false)
    protected Timestamp crDatetime;

    @PrePersist
    protected void onCreate() {
        crDatetime = Timestamp.from(Instant.now());
    }
}
