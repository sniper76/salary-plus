package ag.act.converter;

import ag.act.dto.mysolidarity.InProgressActionUserStatus;
import ag.act.enums.BoardGroup;
import ag.act.model.LinkResponse;
import ag.act.util.AppLinkUrlGenerator;
import org.springframework.stereotype.Component;

import java.util.List;

import static ag.act.dto.mysolidarity.InProgressActionUserStatus.AT_LEAST_ONE_REMAINED;
import static ag.act.dto.mysolidarity.InProgressActionUserStatus.NONE;

@Component
public class ActionLinkResponseConverter {
    private static final String ACTIVE_ACTION_COLOR = "#355CE9";
    private static final String COMPLETED_ACTION_COLOR = "#494b51";
    private final AppLinkUrlGenerator appLinkUrlGenerator;

    public ActionLinkResponseConverter(AppLinkUrlGenerator appLinkUrlGenerator) {
        this.appLinkUrlGenerator = appLinkUrlGenerator;
    }

    public List<ag.act.model.LinkResponse> convert(InProgressActionUserStatus inProgressActionUserStatus, String stockCode) {
        if (inProgressActionUserStatus == NONE) {
            return null;
        }
        if (inProgressActionUserStatus == AT_LEAST_ONE_REMAINED) {
            return List.of(getActiveLinkResponse(stockCode));
        } else {
            return List.of(getCompletedLinkResponse(stockCode));
        }
    }

    private LinkResponse getActiveLinkResponse(String stockCode) {
        return new LinkResponse()
            .title("전자문서")
            .url(appLinkUrlGenerator.generateBoardGroupLinkUrl(stockCode, BoardGroup.ACTION))
            .color(ACTIVE_ACTION_COLOR);
    }

    private LinkResponse getCompletedLinkResponse(String stockCode) {
        return new LinkResponse()
            .title("전자문서(완료)")
            .url(appLinkUrlGenerator.generateBoardGroupLinkUrl(stockCode, BoardGroup.ACTION))
            .color(COMPLETED_ACTION_COLOR);
    }

}
