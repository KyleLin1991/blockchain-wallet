package tw.com.blockchain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * Restful API Response Entity
 * @author Kyle
 * @since 2025/2/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RestApiOneResponse<T> extends BaseRestApiResponse {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 請求處理結果 */
    private T result;
}