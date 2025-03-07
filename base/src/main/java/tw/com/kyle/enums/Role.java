package tw.com.kyle.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    @Schema(description = "系統管理員")
    ADMIN("ROLE_01"),

    @Schema(description = "後台使用者")
    BACK_USER("ROLE_02"),

    @Schema(description = "前台使用者")
    FRONT_USER("ROLE_03");

	private final String value;

    public static Role fromValue(String value) {
        for (Role role : Role.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}