package tw.com.kyle.enums.converter;

import jakarta.persistence.AttributeConverter;
import tw.com.kyle.enums.Role;

import java.util.stream.Stream;

/**
 * @author Kyle
 * @since 2025/3/6
 */
public class RoleConverter implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null) {
            return null;
        }
        return role.getValue();
    }

    @Override
    public Role convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }

        return Stream.of(Role.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }
}