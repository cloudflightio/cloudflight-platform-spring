package io.cloudflight.platform.spring.logging.interceptor;

import io.cloudflight.platform.spring.logging.annotation.LogParam;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Aspect for interception a method call which has one or more parameters annotated
 * with one or more {@link LogParam} annotations.
 *
 * @author Clemens Grabmann
 */
@Aspect
public class LogParamInterceptor {

    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final Map<LogParam, Expression> expressionMap = new ConcurrentHashMap<>();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("execution(public * *..*(.., @io.cloudflight.platform.spring.logging.annotation.LogParams (*), ..)) || "
            + "execution(public * *..*(.., @io.cloudflight.platform.spring.logging.annotation.LogParam (*), ..))")
    public Object addLogParam(final ProceedingJoinPoint joinPoint) throws Throwable {
        Set<String> mdcKeys = new HashSet<>();
        if (joinPoint.getSignature() instanceof MethodSignature methodSignature) {
            Method method = methodSignature.getMethod();
            Parameter[] params = method.getParameters();
            Object[] args = joinPoint.getArgs();
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);

            for (int i = 0; i < params.length; ++i) {
                Parameter param = params[i];
                Object arg = args[i];
                String paramName = parameterNames != null ? parameterNames[i] : param.getName();
                mdcKeys.addAll(processParameter(param, paramName, arg));
            }
        }

        try {
            return joinPoint.proceed();
        } finally {
            for (String key : mdcKeys) {
                MDC.remove(key);
            }
        }
    }

    private Collection<String> processParameter(final Parameter param, String parameterName, final Object arg) {
        Collection<LogParam> logParams = AnnotatedElementUtils.getMergedRepeatableAnnotations(param, LogParam.class);
        Collection<String> mdcKeys = new ArrayList<>(logParams.size());
        for (LogParam logParam : logParams) {
            String name;
            if (StringUtils.isNotBlank(logParam.name())) {
                name = logParam.name();
            } else {
                name = parameterName;
            }

            Object value = arg;
            if (StringUtils.isNotEmpty(logParam.field())) {
                final Expression expression = expressionMap.computeIfAbsent(
                        logParam,
                        k -> expressionParser.parseExpression(logParam.field())
                );
                value = expression.getValue(arg);
                if (StringUtils.isBlank(logParam.name())) {
                    name += ("." + logParam.field());
                }
            }

            MDC.put(name, Objects.toString(value));
            mdcKeys.add(name);
        }

        return mdcKeys;
    }
}
