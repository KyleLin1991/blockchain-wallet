package tw.com.blockchain.misc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.owasp.encoder.Encode;
import tw.com.blockchain.util.AntiSQLUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SafetyHttpRequest wraps the original HTTP request to provide safe parameters values when protect behavior is set
 *
 * @author Kyle
 * @since 2025/2/26
 */
public class SafetyHttpRequest extends HttpServletRequestWrapper {

    private final Map<String, String[]> sanitizedParameterMap;

    /**
     * Constructor to wrap the original HttpServletRequest
     */
    public SafetyHttpRequest(HttpServletRequest req) {
        super(req);
        this.sanitizedParameterMap = sanitizeParameterMap(req.getParameterMap());
    }

    @Override
    public String getQueryString() {
        return AntiSQLUtil.doProcessUnsafeValue(super.getQueryString());
    }

    /**
     * Returns a single safe versioned request parameter value.
     *
     * @param paramName the request parameter name
     * @return String a single safe versioned request value
     */
    @Override
    public String getParameter(String paramName) {
        String[] values = sanitizedParameterMap.get(paramName);

        return (ArrayUtils.isNotEmpty(values)) ? values[0] : null;
    }

    /**
     * Returns an array of safe versioned request values.
     *
     * @param paramName the request parameter name
     * @return String[] an array of versioned request values
     */
    @Override
    public String[] getParameterValues(String paramName) {
        return sanitizedParameterMap.get(paramName);
    }

    /**
     * Returns a safe versioned request parameter Map.
     *
     * @return Map<String, String [ ]> a safe versioned request parameter Map.
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        return sanitizedParameterMap;
    }

    /**
     * 過濾所有請求參數，防止 SQL Injection 和 XSS 攻擊
     */
    private Map<String, String[]> sanitizeParameterMap(Map<String, String[]> parameterMap) {
        if (parameterMap == null || parameterMap.isEmpty()) {
            return Collections.emptyMap();
        }

        return parameterMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // 保留原本的 key
                        entry -> sanitizeValues(entry.getValue()), // 過濾 value
                        (existing, replacement) -> existing, // 避免 key 衝突
                        HashMap::new // 使用 HashMap 儲存
                ));
    }

    /**
     * 過濾參數值，去除空格並執行 SQL Injection/XSS 過濾
     */
    private String[] sanitizeValues(String[] values) {
        if (ArrayUtils.isEmpty(values)) {
            return new String[0];
        }
        return Arrays.stream(values)
                .map(value -> {
                    value = StringUtils.strip(value); // 去除頭尾空格
                    value = AntiSQLUtil.doProcessUnsafeValue(value); // SQL Injection 過濾
                    value = Encode.forHtmlContent(value); // XSS 過濾
                    return value;
                })
                .toArray(String[]::new);
    }

}