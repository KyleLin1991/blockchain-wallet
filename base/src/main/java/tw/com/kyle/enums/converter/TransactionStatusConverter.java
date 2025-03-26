package tw.com.kyle.enums.converter;

import jakarta.persistence.AttributeConverter;
import tw.com.kyle.enums.TransactionStatus;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Kyle
 * @since 2025/3/6
 */
public class TransactionStatusConverter implements AttributeConverter<TransactionStatus, String> {
    @Override
    public String convertToDatabaseColumn(TransactionStatus status) {
        if (status == null) {
            return null;
        }
        return status.getValue();
    }

    @Override
    public TransactionStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(TransactionStatus.values())
                .filter(c -> Objects.equals(c.getCode(), code))
                .findFirst()
                .orElse(null);
    }
}