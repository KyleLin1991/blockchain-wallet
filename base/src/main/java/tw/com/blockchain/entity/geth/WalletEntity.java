package tw.com.blockchain.entity.geth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import tw.com.blockchain.entity.BaseTimeEntity;
import tw.com.blockchain.entity.auth.UserEntity;

import java.math.BigInteger;

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

    @Column(name = "balance")
    private BigInteger balance;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity userEntity;

}
