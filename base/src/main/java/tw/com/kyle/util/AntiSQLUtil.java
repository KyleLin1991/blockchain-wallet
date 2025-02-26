package tw.com.kyle.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL Injection攻擊防禦 Utility
 * @author Kyle
 * @since 2025/2/26
 */
@UtilityClass
public class AntiSQLUtil {
    /** Anti SQL keywords */
    private static final List<String> KEYWORDS = new ArrayList<String>();

    static {
        // KEYWORDS.add(";");
        KEYWORDS.add("\'");
        // KEYWORDS.add("/*");
        // KEYWORDS.add("*/");
        KEYWORDS.add("--");
        // KEYWORDS.add("exec");
        // KEYWORDS.add("select");
        // KEYWORDS.add("update");
        // KEYWORDS.add("delete");
        // KEYWORDS.add("insert");
        // KEYWORDS.add("alter");
        // KEYWORDS.add("drop");
        // KEYWORDS.add("create");
        // KEYWORDS.add("shutdown");

        // KEYWORDS.add("chr");
        // KEYWORDS.add("char");
    }

    /**
     * 判斷是否包含不安全的文字
     * @param value check value
     * @return boolean
     */
    public boolean isUnsafe(String value) {
        String containKeyword = KEYWORDS.stream().filter(keyword -> StringUtils.containsIgnoreCase(value, keyword)).findFirst().orElse(null);

        return StringUtils.isNotBlank(containKeyword);
    }

    /**
     * 判斷傳入的參數是否為不安全的字串，若是的話，將第1個字轉成全形，若不是的話，則原值回傳。
     * @param value check value
     * @return String
     */
    public String doProcessUnsafeValue(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        if (isUnsafe(value)) {
            return doConvert2SafeValue(value);
        }

        return value;
    }

    /**
     * 將轉入的字串第1個字轉成全形
     * @param value check value
     * @return String
     */
    public String doConvertFistChar2Full(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        char[] charArray = value.toCharArray();

        int ic = charArray[0];
        if (ic >= 33 && ic <= 126) {
            charArray[0] = (char) (ic + 65248);
        } else if (ic == 32) {
            charArray[0] = (char) 12288;
        }

        return new String(charArray);
    }

    /**
     * 若傳入的字串含不安全的字串，則將不安全字串的第1個字元轉為全形
     * @param value check value
     * @return String
     */
    private String doConvert2SafeValue(String value) {
        value = StringUtils.defaultString(value);
        StringBuilder result = new StringBuilder(value);
        String lowerCase = value.toLowerCase();

        for (String keyWorld : KEYWORDS) {
            int x = -1;
            while ((x = lowerCase.indexOf(keyWorld)) >= 0) {
                if (keyWorld.length() == 1) {
                    result.replace(x, x + 1, doConvertFistChar2Full(result.substring(x, x + 1)));
                    lowerCase = result.toString().toLowerCase();
                    continue;
                }

                result.replace(x, x + 1, doConvertFistChar2Full(result.substring(x, x + 1)));
                lowerCase = result.toString().toLowerCase();
            }
        }

        return result.toString();
    }
}