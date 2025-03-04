package tw.com.kyle.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserType {

    @Schema(description = "前台使用者")
    FRONT_USER("0"),

    @Schema(description = "後台使用者")
    BACK_USER("1");

	private final String value;
}