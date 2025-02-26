package tw.com.kyle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Restful API Response Entity
 * @author Kyle
 * @since 2025/2/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseRestApiResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 處理結果狀態碼，請參閱HTTP Status code */
    private String status = "200"; // 200: Success

    /** 服務端告知呼叫端訊息內容清單 */
    private List<String> msgs = new ArrayList<>();
}
