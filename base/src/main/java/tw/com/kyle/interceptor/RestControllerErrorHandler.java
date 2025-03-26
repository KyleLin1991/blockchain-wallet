package tw.com.kyle.interceptor;

import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import tw.com.kyle.dto.BaseRestApiResponse;

import java.sql.SQLException;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author Kyle
 * @since 2025/2/26
 */
@CommonsLog
@RestControllerAdvice
public class RestControllerErrorHandler {

    /**
     * 統一處理未受處理的異常錯誤
     *
     * @param e 異常訊息
     * @return BaseRestApiResponse 處理結果
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public BaseRestApiResponse doHandleException(Exception e) {
        if (log.isErrorEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("message", e.getMessage());
            logParams.put("stackTrace", ExceptionUtils.getStackTrace(e));

            log.error(logParams);
        }
        // 錯誤印出
        log.error("Unexpected error occurred", e);

        BaseRestApiResponse response = new BaseRestApiResponse();
        response.setStatus("500");
        response.setMsgs(Collections.singletonList(e.getMessage()));

        return response;
    }

    /**
     * 處理ResponseStatusException錯誤訊息，此method Java程式拋出Exception之status及特定message
     *
     * @param e 異常訊息
     * @return BaseRestApiResponse 處理結果
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ResponseStatusException.class)
    public BaseRestApiResponse doHandleResponseStatusException(ResponseStatusException e) {
        if (log.isErrorEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("message", e.getMessage());
            logParams.put("stackTrace", ExceptionUtils.getStackTrace(e));

            log.error(logParams);
        }
        // 錯誤印出
        log.error("Unexpected error occurred", e);

        BaseRestApiResponse response = new BaseRestApiResponse();
        response.setStatus(String.valueOf(e.getStatusCode().value()));
        response.setMsgs(Collections.singletonList(e.getReason()));

        return response;
    }

    /**
     * 處理欄位驗證失敗錯誤訊息，此method處理Client使用@RequestBody傳入的參數驗證
     *
     * @param e 異常訊息
     * @return BaseRestApiResponse 處理結果
     */
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseRestApiResponse doHandleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        if (log.isErrorEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("message", e.getMessage());
            logParams.put("stackTrace", ExceptionUtils.getStackTrace(e));

            log.error(logParams);
        }
        // 錯誤印出
        log.error("Unexpected error occurred", e);

        BaseRestApiResponse response = new BaseRestApiResponse();
        response.setStatus("412"); // 412: Precondition Failed
        response.setMsgs(e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()));

        return response;
    }

    /**
     * 處理欄位驗證失敗錯誤訊息，此method處理Client非使用@RequestBody傳入的參數驗證
     *
     * @param e 異常訊息
     * @return BaseRestApiResponse 處理結果
     */
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(BindException.class)
    public BaseRestApiResponse doHandleBindException(BindException e) {
        if (log.isErrorEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("message", e.getMessage());
            logParams.put("stackTrace", ExceptionUtils.getStackTrace(e));

            log.error(logParams);
        }
        // 錯誤印出
        log.error("Unexpected error occurred", e);

        BaseRestApiResponse response = new BaseRestApiResponse();
        response.setStatus("412"); // 412: Precondition Failed
        response.setMsgs(e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()));

        return response;
    }
}
