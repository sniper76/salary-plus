package ag.act.core.aop;

import ag.act.model.GetBoardGroupPostResponse;
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
public class TemporaryViewUserCountSortOverrideAspect {
    // TODO 이 클래스는 임시로 viewUserCount를 viewCount로 바꾸는 용도로 사용되고 있습니다.
    //      나중에 전체적으로 다시 viewUserCount를 사용하는 것으로 바뀌면 이 클래스는 삭제해야 합니다. 2023-12-18

    private static final String VIEW_USER_COUNT_SORT_PREFIX = "viewUserCount:";
    private static final String VIEW_COUNT_SORT_PREFIX = "viewCount:";
    private static final String SORTS_PARAMETER_KEY = "sorts";

    @Around("execution(* ag.act.handler.stockboardgrouppost.StockBoardGroupPostApiDelegateImpl.getBoardGroupPosts(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {

        Map<String, Object> parameterMap = AspectParameterUtil.findParameters(joinPoint);
        List<String> originalSorts = (List<String>) parameterMap.get(SORTS_PARAMETER_KEY);
        Optional<String> viewUserCountSortOptional = findViewUserCountSort(originalSorts);

        if (viewUserCountSortOptional.isEmpty()) {
            return joinPoint.proceed();
        }

        return executeAndOverrideSorts(joinPoint, parameterMap, originalSorts, viewUserCountSortOptional.get());
    }

    @Nullable
    private ResponseEntity<GetBoardGroupPostResponse> executeAndOverrideSorts(
        ProceedingJoinPoint joinPoint,
        Map<String, Object> parameterMap,
        List<String> originalSorts,
        String viewUserCountSort
    ) throws Throwable {
        parameterMap.put(SORTS_PARAMETER_KEY, getOverriddenSortList(originalSorts, viewUserCountSort));

        ResponseEntity<GetBoardGroupPostResponse> result
            = (ResponseEntity<GetBoardGroupPostResponse>) joinPoint.proceed(parameterMap.values().toArray());

        if (result != null) {
            result.getBody().getPaging().sorts(originalSorts);
        }

        return result;
    }

    private List<String> getOverriddenSortList(List<String> originalSorts, String viewUserCountSort) {
        List<String> sortList = new ArrayList<>(originalSorts);
        sortList.set(sortList.indexOf(viewUserCountSort), viewUserCountSort.replace(VIEW_USER_COUNT_SORT_PREFIX, VIEW_COUNT_SORT_PREFIX));

        return sortList;
    }

    private Optional<String> findViewUserCountSort(List<String> originalSorts) {
        return originalSorts.stream()
            .filter(sort -> sort.startsWith(VIEW_USER_COUNT_SORT_PREFIX))
            .findFirst();
    }
}
