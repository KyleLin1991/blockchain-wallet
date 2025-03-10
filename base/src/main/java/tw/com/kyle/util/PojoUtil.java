package tw.com.kyle.util;

import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Kyle
 * @since 2025/3/7
 */
@UtilityClass
public class PojoUtil {

    /*
     * 可忽略空值的複製方法
     */
    public static void copyProperties(Object src, Object target, Boolean ignoreNull) {
        if (ignoreNull) {
            BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
        } else {
            BeanUtils.copyProperties(src, target);
        }
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
