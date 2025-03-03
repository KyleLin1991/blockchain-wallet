package tw.com.kyle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

/**
 * @author Kyle
 * @since 2025/2/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RestApiListResponse<T> extends BaseRestApiResponse {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 請求處理結果清單 */
    private List<T> result;
}