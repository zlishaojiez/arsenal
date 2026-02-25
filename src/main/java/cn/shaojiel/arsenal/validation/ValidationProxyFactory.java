package cn.shaojiel.arsenal.validation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.groups.Default;

import org.apache.commons.lang3.ArrayUtils;

public final class ValidationProxyFactory {

    private ValidationProxyFactory() {}

    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private static final ExecutableValidator EXECUTABLE_VALIDATOR = VALIDATOR_FACTORY.getValidator().forExecutables();

    static {
        // 在 JVM 关闭时自动关闭 Factory
        Runtime.getRuntime().addShutdownHook(new Thread(VALIDATOR_FACTORY::close));
    }

    /**
     * 为接口创建带有参数验证的代理
     *
     * @param target 目标实现类
     * @param interfaceType 接口类型
     * @param groups 可选的验证分组
     * @return 带验证功能的代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T createValidationProxy(final T target, final Class<T> interfaceType, final Class<?>... groups) {
        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[] {interfaceType},
                new ValidationInvocationHandler(target, groups)
        );
    }

    private static class ValidationInvocationHandler implements InvocationHandler {

        private final Object target;
        private final Class<?>[] groups;

        ValidationInvocationHandler(final Object target, final Class<?>... groups) {
            this.target = target;
            this.groups = groups;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            // 执行参数验证
            validateParameters(method, args, this.groups);

            try {
                return method.invoke(this.target, args);
            } catch (final InvocationTargetException e) {
                final Throwable cause = e.getCause();
                if (cause != null) {
                    throw cause;
                }

                throw e;
            }
        }

        private void validateParameters(final Method method, final Object[] args, final Class<?>... groups) {
            if (ArrayUtils.isEmpty(args)) {
                return;
            }
            final Set<Class<?>> resultGroups = new HashSet<>();
            // 默认分组必须包含
            resultGroups.add(Default.class);
            if (ArrayUtils.isNotEmpty(groups)) {
                resultGroups.addAll(Arrays.asList(groups));
            }

            final Set<ConstraintViolation<Object>> violations = EXECUTABLE_VALIDATOR
                    .validateParameters(this.target, method, args, resultGroups.toArray(new Class<?>[0]));

            if (!violations.isEmpty()) {
                final String methodName = method.getName();
                final Parameter[] parameters = method.getParameters();
                final String errorMessage = violations.stream()
                        .map(v -> {
                            final String paramName = extractParameterName(v.getPropertyPath(), parameters);
                            return String.format("%s: %s", paramName, v.getMessage());
                        })
                        .collect(Collectors.joining("; "));

                throw new IllegalArgumentException("方法【%s】参数校验失败: %s".formatted(methodName, errorMessage));
            }
        }

        /**
         * 从 PropertyPath 中提取参数名
         * PropertyPath 格式通常为: methodName.arg0.fieldName 或 methodName.arg0
         */
        private String extractParameterName(final Path propertyPath, final Parameter[] parameters) {
            final StringBuilder result = new StringBuilder();

            for (final Path.Node node : propertyPath) {

                switch (node.getKind()) {
                    case METHOD -> {
                        // 跳过方法节点
                    }
                    case PARAMETER -> {
                        // 处理参数节点
                        final int paramIndex = node.as(Path.ParameterNode.class)
                                .getParameterIndex();

                        final Parameter param = parameters[paramIndex];
                        if (param.isNamePresent()) {
                            result.append(param.getName());
                        } else {
                            // 如果参数名不可用，使用 argN 形式
                            result.append("arg").append(paramIndex);
                        }
                    }
                    default -> {
                        // 其他节点类型（如字段、属性等）直接使用名称
                        if (node.getName() != null) {
                            if (!result.isEmpty()) {
                                result.append(".");
                            }
                            result.append(node.getName());
                        }
                    }
                }
            }

            return result.toString();
        }
    }

}
