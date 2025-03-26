package tw.com.kyle.entity.geth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import tw.com.kyle.entity.CreateTimeEntity;
import tw.com.kyle.enums.TransactionStatus;
import tw.com.kyle.enums.converter.TransactionStatusConverter;

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
@Schema(description = "入金")
@Table(name = "deposit", schema = "geth")
public class DepositEntity extends CreateTimeEntity {

    @Column(name = "\"from\"", nullable = false, length = 128)
    private String from;

    @Column(name = "\"to\"", nullable = false, length = 128)
    private String to;

    @Column(name = "balance", nullable = false)
    private BigInteger balance;

    @Column(name = "status", nullable = false)
    @Convert(converter = TransactionStatusConverter.class)
    private TransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_hash", nullable = false, updatable = false)
    private TransactionEntity transaction;
}
