package tw.com.kyle;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import tw.com.kyle.dto.RestApiOneResponse;

/**
 * @author Kyle
 * @since 2025/2/26
 */
@Getter
@CommonsLog
public class BaseController {

    /**
     * default error message
     */
    @Value("${runtime.default-err-msg}")
    private String defaultErrMsg;

    /**
     * 運行環境
     */
    @Value("${runtime.environment}")
    private String runtimeEnv;

    /**
     * Spring application context
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * HTTP Session
     */
    @Setter
    @Autowired
    private HttpSession session;

    /**
     * HTTP Request
     */
    @Autowired
    private HttpServletRequest request;

    /**
     * HTTP Response
     */
    @Autowired
    private HttpServletResponse response;

    /**
     * JSR-303 Validator
     */
    @Autowired
    private Validator validator;

    /**
     * 取得預設單筆執行成功結果
     *
     * @param body 請求處理結果內容
     * @return RestApiOneResponse<T> 處理結果
     */
    protected final <T> RestApiOneResponse<T> doGetDefaultOneResult(T body) {
        if (log.isTraceEnabled()) {
            JSONObject logParams = new JSONObject().put("body", body);
            log.trace(logParams);
        }
        RestApiOneResponse<T> response = new RestApiOneResponse<T>();
        response.setResult(body);

        return response;
    }
}
