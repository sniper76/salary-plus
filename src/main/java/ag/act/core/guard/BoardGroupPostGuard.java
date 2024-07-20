package ag.act.core.guard;


import ag.act.exception.ActRuntimeException;
import ag.act.util.StatusUtil;
import ag.act.validator.post.StockBoardGroupPostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class BoardGroupPostGuard implements ActGuard {

    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;

    public void canActivate(Map<String, Object> parameterMap) throws ActRuntimeException {
        final String stockCode = (String) parameterMap.get("stockCode");
        final String boardGroupName = (String) parameterMap.get("boardGroupName");
        final Long postId = (Long) parameterMap.get("postId");

        stockBoardGroupPostValidator.validateBoardGroupPost(postId, stockCode, boardGroupName, StatusUtil.getDeleteStatuses());
    }
}
