package tw.com.kyle.entity.geth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import tw.com.kyle.entity.CreateTimeEntity;

import java.math.BigInteger;

/**
 * @author Kyle
 * @since 2025/3/18
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity
@Schema(description = "交易")
@Table(name = "transaction", schema = "geth")
public class TransactionEntity extends CreateTimeEntity {

    @Column(name = "hash", nullable = false)
    private byte[] hash;

    @Column(name = "nonce", nullable = false)
    private BigInteger nonce;

    @Column(name = "\"from\"", nullable = false, length = 128)
    private String from;

    @Column(name = "\"to\"", nullable = false, length = 128)
    private String to;

    @Column(name = "gas_price", nullable = false)
    private BigInteger gasPrice;

    @Column(name = "gas", nullable = false)
    private BigInteger gas;

    @Column(name = "gas_used", nullable = false)
    private BigInteger gasUsed;

    @Column(name = "status", nullable = false)
    private String status;
}
