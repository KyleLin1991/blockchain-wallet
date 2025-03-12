package tw.com.kyle.entity.geth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import tw.com.kyle.entity.BaseTimeEntity;
import tw.com.kyle.entity.auth.UserEntity;

import java.math.BigDecimal;

/**
 * @author Kyle
 * @since 2025/3/12
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity
@Schema(description = "錢包")
@Table(name = "wallet", schema = "geth")
public class WalletEntity extends BaseTimeEntity {

    @Column(name = "address", unique = true, nullable = false, length = 128)
    private String address;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "balance")
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity userEntity;

}
