package tw.com.blockchain.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionStatus {

    @Schema(description = "待處理")
    PENDING(null, "0"),

    @Schema(description = "成功")
    SUCCESS("0x1", "1"),

    @Schema(description = "失敗")
    FAIL("0x0", "2");

	private final String code;
	private final String value;
}