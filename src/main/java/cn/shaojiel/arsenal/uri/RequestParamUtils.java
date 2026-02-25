package cn.shaojiel.arsenal.uri;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RequestParamUtils {

    private RequestParamUtils() {
    }

    /**
     * 添加单个查询参数，适用于非 POJO 类型（如 String, Integer 等）
     *
     * @param uriBuilder UriComponentsBuilder
     * @param paramName  URL 参数名
     * @param value      参数值
     */
    public static void addQueryParam(final UriComponentsBuilder uriBuilder, final String paramName, final Object value) {
        if (StringUtils.isNotBlank(paramName) && value != null) {
            final String strValue = String.valueOf(value);
            if (StringUtils.isNotBlank(strValue)) {
                uriBuilder.queryParam(paramName, strValue);
            }
        }
    }

    /**
     * 根据请求对象的字段添加查询参数，适用于 POJO 类型
     *
     * @param uriBuilder UriComponentsBuilder
     * @param request    请求对象
     * @param <T>        请求对象类型
     */
    public static <T> void addQueryParams(final UriComponentsBuilder uriBuilder, final T request) {
        if (request == null) {
            return;
        }

        Class<?> clazz = request.getClass();
        while (clazz != null) {
            final Field[] fields = clazz.getDeclaredFields();
            for (final Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                Object value = null;
                try {
                    final PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                    final Method readMethod = pd.getReadMethod();
                    if (readMethod != null && readMethod.canAccess(request)) {
                         value = readMethod.invoke(request);
                    }

                } catch (final IntrospectionException e) {
                    // PropertyDescriptor failed (e.g. non-standard getter name), fallback to direct access
                    log.debug("Introspection failed for field: {}, falling back to direct field access.", field.getName());
                    value = getFieldValueDirectly(field, request);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    // Ignore exception and continue
                    log.debug("Exception occurs when processing field: {} on class {} to get HTTP request parameter",
                            field.getName(), clazz.getName(), e);
                    continue;
                }

                addQueryParam(uriBuilder, field, value);
            }

            clazz = clazz.getSuperclass();
        }

    }

    private static Object getFieldValueDirectly(final Field field, final Object target) {
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (final IllegalAccessException e) {
            log.debug("Failed to access field directly: {}", field.getName(), e);
            return null;
        }
    }

    private static void addQueryParam(final UriComponentsBuilder uriBuilder, final Field field, final Object value) {
        if (value != null) {
            if (Collection.class.isAssignableFrom(field.getType())) {
                final String joinedValue = ((Collection<?>) value).stream()
                        .map(String::valueOf)
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.joining(","));
                uriBuilder.queryParam(field.getName(), joinedValue);
            } else {
                if (StringUtils.isNotBlank(String.valueOf(value))) {
                    uriBuilder.queryParam(field.getName(), String.valueOf(value));
                }
            }
        }
    }
}
