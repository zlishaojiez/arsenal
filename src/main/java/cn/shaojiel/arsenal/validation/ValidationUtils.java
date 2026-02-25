package cn.shaojiel.arsenal.validation;

import jakarta.validation.ConstraintValidatorContext;

/**
 * 验证工具类
 */
public final class ValidationUtils {

    private ValidationUtils() {}

    /**
     * 构建自定义约束违反信息
     *
     * @param context 约束验证上下文
     * @param messageTemplate 错误信息模板
     * @param propertyNode 属性节点名称
     */
    public static void buildConstraintViolation(final ConstraintValidatorContext context, final String messageTemplate,
            final String propertyNode) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addPropertyNode(propertyNode)
                .addConstraintViolation();
    }
}
