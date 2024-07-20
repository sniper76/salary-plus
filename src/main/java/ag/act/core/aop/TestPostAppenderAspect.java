package ag.act.core.aop;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.service.post.TestPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class TestPostAppenderAspect {
    private final TestPostService testPostService;

    @AfterReturning(
        pointcut = "execution(* ag.act.service.stockboardgrouppost.StockBoardGroupPostService.getBoardGroupPosts(..))",
        returning = "result")
    public Object logAfterMethodCall(JoinPoint joinPoint, Object result) {
        return ActUserProvider.get()
            .map(user -> getResultWithTestPosts(joinPoint, result))
            .orElse(result);
    }

    private Object getResultWithTestPosts(JoinPoint joinPoint, Object result) {
        try {
            if (result instanceof GetBoardGroupPostResponse getBoardGroupPostResponse) {
                GetBoardGroupPostDto arg = (GetBoardGroupPostDto) joinPoint.getArgs()[0];
                List<ag.act.model.PostResponse> newList = new ArrayList<>();
                newList.addAll(testPostService.getTestPostList(arg));
                newList.addAll(getBoardGroupPostResponse.getData());

                return getBoardGroupPostResponse.data(newList);
            }
        } catch (Throwable ex) {
            log.error("Error occurred while applying TestPostAppenderAspect", ex);
        }
        return result;
    }
}
