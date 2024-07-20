package ag.act.converter;

import ag.act.model.UnreadStockBoardGroupPostStatusResponse;
import org.springframework.stereotype.Component;

@Component
public class UnreadStockBoardGroupPostStatusConverter {

    public UnreadStockBoardGroupPostStatusResponse convert(
        Boolean unreadAnalysis,
        Boolean unreadAction,
        Boolean unreadDebate
    ) {
        return new UnreadStockBoardGroupPostStatusResponse()
            .unreadAnalysis(unreadAnalysis)
            .unreadAction(unreadAction)
            .unreadDebate(unreadDebate);
    }
}
