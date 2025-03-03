package tw.com.kyle.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    @Schema(description = "停用")
    DISABLE("0"),

    @Schema(description = "啟用")
    ENABLE("1"),;

	private final String value;
}