package tw.com.blockchain.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserResultMsg {

    @Schema(description = "新增成功")
    ADD_SUCCESS("新增成功"),

    @Schema(description = "更新成功")
    UPDATE_SUCCESS("更新成功");

	private final String value;

    public static UserResultMsg fromValue(String value) {
        for (UserResultMsg role : UserResultMsg.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}