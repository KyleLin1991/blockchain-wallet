package tw.com.kyle.enums.converter;

import jakarta.persistence.AttributeConverter;
import tw.com.kyle.enums.EnableStatus;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Kyle
 * @since 2025/3/6
 */
public class EnableStatusConverter implements AttributeConverter<EnableStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(EnableStatus userStatus) {
        if (userStatus == null) {
            return null;
        }
        return userStatus.getValue();
    }

    @Override
    public EnableStatus convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }

        return Stream.of(EnableStatus.values())
                .filter(c -> Objects.equals(c.getValue(), value))
                .findFirst()
                .orElse(null);
    }
}