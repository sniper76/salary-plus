package ag.act.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class AspectParameterUtil {

    public static Map<String, Object> findParameters(JoinPoint joinPoint) {

        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Object[] args = joinPoint.getArgs();
        final String[] parameterNames = methodSignature.getParameterNames();
        final Map<String, Object> parameterMap = new LinkedHashMap<>();

        for (int i = 0; i < parameterNames.length; i++) {
            parameterMap.put(parameterNames[i], args[i]);
        }

        return parameterMap;
    }

    public static <T> Optional<T> findParameter(JoinPoint joinPoint, Class<? extends Annotation> annotation, Class<T> targetClass) {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Parameter[] parameters = methodSignature.getMethod().getParameters();
        final Object[] args = joinPoint.getArgs();

        final OptionalInt parameterIndexOpt = findIndex(annotation, parameters);

        if (parameterIndexOpt.isPresent()) {
            final int index = parameterIndexOpt.getAsInt();
            if (targetClass.isInstance(args[index])) {
                return Optional.of(targetClass.cast(args[index]));
            }
        }

        return Optional.empty();
    }

    private static OptionalInt findIndex(final Class<? extends Annotation> annotation, final Parameter[] parameters) {
        return IntStream.range(0, parameters.length)
            .filter(i -> parameters[i].isAnnotationPresent(annotation))
            .findFirst();
    }
}
