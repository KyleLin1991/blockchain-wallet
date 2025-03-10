package tw.com.kyle.service;

import io.swagger.v3.oas.annotations.media.Schema;
import org.reflections.Reflections;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Kyle
 * @since 2025/3/4
 */
@Service
public class CommonService {

    public Map<String, List<Map<String, Object>>> getAllEnums() {
        Reflections reflections = new Reflections("tw.com.kyle.enums"); // 指定包名
        Set<Class<? extends Enum>> enums = reflections.getSubTypesOf(Enum.class);

        Map<String, List<Map<String, Object>>> allEnumData = new HashMap<>();

        for (Class<? extends Enum> enumClass : enums) {
            List<Map<String, Object>> enumList = new ArrayList<>();

            try {
                for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
                    Map<String, Object> enumData = new HashMap<>();
                    enumData.put("enum", enumConstant.name()); // 枚举名称

                    // 获取 value 值
                    Object value = getEnumValue(enumConstant, enumClass);
                    if (value != null) {
                        enumData.put("value", value);
                    }

                    // 读取 @Schema(description = "...") 注解
                    Field enumField = enumClass.getField(enumConstant.name());
                    Schema schema = enumField.getAnnotation(Schema.class);
                    if (schema != null) {
                        enumData.put("description", schema.description());
                    }

                    enumList.add(enumData);
                }
            } catch (Exception e) {
                throw new RuntimeException("無法解析enum: " + enumClass.getName(), e);
            }

            allEnumData.put(enumClass.getSimpleName(), enumList);
        }

        return allEnumData;
    }

    /**
     * 通过反射取得 Enum 的 value（支持任意类型）
     */
    private static Object getEnumValue(Enum<?> enumConstant, Class<? extends Enum> enumClass) {
        for (Field field : enumClass.getDeclaredFields()) {
            if (!field.isEnumConstant()) {
                field.setAccessible(true);
                try {
                    return field.get(enumConstant);
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        return null;
    }
}
