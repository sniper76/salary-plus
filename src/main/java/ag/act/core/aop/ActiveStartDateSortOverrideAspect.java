package ag.act.core.aop;

import ag.act.dto.Pageable;
import ag.act.util.AspectParameterUtil;
import jakarta.annotation.Nullable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"unchecked", "DataFlowIssue"})
@Aspect
@Component
public class ActiveStartDateSortOverrideAspect {
    private static final String ORIGINAL_SORT_PREFIX = "createdAt:";
    private static final String NEW_SORT_PREFIX = "activeStartDate:";
    private static final String SORTS_KEY = "sorts";

    @Around("""
        execution(* ag.act.handler.stockboardgrouppost.StockBoardGroupPostApiDelegateImpl.getBoardGroupPosts(..)) ||
        execution(* ag.act.handler.UserNotificationApiDelegateImpl.getUserNotifications(..))
        """)
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {

        Map<String, Object> parameterMap = AspectParameterUtil.findParameters(joinPoint);
        List<String> originalSorts = (List<String>) parameterMap.get(SORTS_KEY);
        Optional<String> originalSortOptional = findOriginalSort(originalSorts);

        if (originalSortOptional.isEmpty()) {
            return joinPoint.proceed();
        }

        return executeAndOverrideSorts(joinPoint, parameterMap, originalSorts, originalSortOptional.get());
    }

    @Nullable
    private ResponseEntity<Pageable> executeAndOverrideSorts(
        ProceedingJoinPoint joinPoint,
        Map<String, Object> parameterMap,
        List<String> originalSorts,
        String viewUserCountSort
    ) throws Throwable {
        parameterMap.put(SORTS_KEY, getOverriddenSortList(originalSorts, viewUserCountSort));

        ResponseEntity<Pageable> result
            = (ResponseEntity<Pageable>) joinPoint.proceed(parameterMap.values().toArray());

        if (result != null) {
            result.getBody().getPaging().sorts(originalSorts);
        }

        return result;
    }

    private List<String> getOverriddenSortList(List<String> originalSorts, String viewUserCountSort) {
        List<String> sortList = new ArrayList<>(originalSorts);
        sortList.set(sortList.indexOf(viewUserCountSort), viewUserCountSort.replace(ORIGINAL_SORT_PREFIX, NEW_SORT_PREFIX));

        return sortList;
    }

    private Optional<String> findOriginalSort(List<String> originalSorts) {
        return originalSorts.stream()
            .filter(sort -> sort.startsWith(ORIGINAL_SORT_PREFIX))
            .findFirst();
    }
}
