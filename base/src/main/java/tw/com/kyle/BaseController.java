package tw.com.kyle;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import tw.com.kyle.dto.RestApiListResponse;
import tw.com.kyle.dto.RestApiOneResponse;

import java.util.List;

/**
 * @author Kyle
 * @since 2025/2/26
 */
@Setter
@Getter
@CommonsLog
public class BaseController {

    /**
     * HTTP Session
     */
    @Autowired
    private HttpSession session;

    /**
     * 取得單筆執行成功結果
     *
     * @param body 請求處理結果內容
     * @return RestApiOneResponse<T> 處理結果
     */
    protected final <T> RestApiOneResponse<T> doGetOneResult(T body) {
        if (log.isTraceEnabled()) {
            JSONObject logParams = new JSONObject().put("body", body);
            log.trace(logParams);
        }
        RestApiOneResponse<T> response = new RestApiOneResponse<>();
        response.setResult(body);

        return response;
    }

    /**
     * 取得預設多筆筆執行成功結果
     *
     * @param body 請求處理結果內容清單
     * @return RestApiOneResponse<T> 處理結果
     */
    protected final <T> RestApiListResponse<T> doGetListResult(List<T> body) {
        if (log.isTraceEnabled()) {
            JSONObject logParams = new JSONObject().put("body", body);
            log.trace(logParams);
        }

        RestApiListResponse<T> response = new RestApiListResponse<T>();
        response.setResult(body);

        return response;
    }
}
