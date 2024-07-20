package ag.act.core.aop;

import ag.act.core.annotation.PageableOverrider;
import ag.act.exception.InternalServerException;
import ag.act.util.AspectParameterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PageableOverriderAspect {
    private static final Pattern sortPattern = Pattern.compile("^([a-zA-Z]+):(?i)(asc|desc)$");
    private static final String SIZE = "size";
    private static final String SORTS = "sorts";


    @Around("@annotation(ag.act.core.annotation.PageableOverrider)")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        return overrideContents(joinPoint, findMethodAnnotation(joinPoint));
    }

    private Object overrideContents(ProceedingJoinPoint joinPoint, PageableOverrider pageableOverrider) throws Throwable {

        Object[] args;
        try {
            final Map<String, Object> parameters = AspectParameterUtil.findParameters(joinPoint);
            final List<String> possibleSortNames = Arrays.asList(pageableOverrider.possibleSortNames());

            overrideSize(parameters, pageableOverrider.MAX_SIZE, pageableOverrider.defaultSize());
            overrideSorts(parameters, possibleSortNames, pageableOverrider.defaultSort());

            args = parameters.values().toArray();
        } catch (Throwable ex) {
            log.error("Error occurred while applying @PageableOverrider", ex);
            throw new InternalServerException("데이터를 가공하는 중에 알 수 없는 오류가 발생하였습니다.", ex);
        }
        return joinPoint.proceed(args);
    }

    @SuppressWarnings("unchecked")
    private void overrideSorts(Map<String, Object> parameters, List<String> possibleSortNames, String defaultSort) {
        if (!parameters.containsKey(SORTS)) {
            return;
        }

        List<String> sorts = (List<String>) parameters.getOrDefault(SORTS, List.of());

        if (!isAllSortMatched(possibleSortNames, sorts)) {
            sorts = List.of(defaultSort);
        }

        parameters.put(SORTS, sorts);
    }

    private boolean isAllSortMatched(List<String> possibleSortNames, List<String> sorts) {
        if (sorts.isEmpty()) {
            return false;
        }
        return sorts.stream()
            .allMatch(sort -> {
                Matcher matcher = sortPattern.matcher(sort);
                if (!matcher.matches()) {
                    return false;
                }
                final String sortName = matcher.group(1);
                return possibleSortNames.contains(sortName);
            });
    }

    private void overrideSize(Map<String, Object> parameters, int maxSize, int defaultSize) {
        if (!parameters.containsKey(SIZE)) {
            return;
        }

        Integer size = (Integer) parameters.getOrDefault(SIZE, defaultSize);

        if (size > maxSize) {
            size = maxSize;
        }

        if (size <= 0) {
            size = defaultSize;
        }

        parameters.put(SIZE, size);
    }

    private PageableOverrider findMethodAnnotation(ProceedingJoinPoint joinPoint) {

        final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        final PageableOverrider pageableOverrider = AnnotationUtils.findAnnotation(method, PageableOverrider.class);
        Objects.requireNonNull(pageableOverrider, "Can not find PageableOverrider annotation on method");

        return pageableOverrider;
    }
}
