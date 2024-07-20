package ag.act.converter.popup;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.Popup;
import ag.act.entity.Post;
import ag.act.enums.AppLinkType;
import ag.act.enums.BoardCategory;
import ag.act.enums.popup.PopupDisplayTargetType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.PopupRequest;
import ag.act.model.Status;
import ag.act.util.AppLinkUrlManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopupRequestConverter {
    private final AppLinkUrlManager appLinkUrlManager;

    public Popup convert(PopupRequest popupRequest, Post post) {
        Popup popup = new Popup();
        return convert(popupRequest, popup, post);
    }

    public Popup convert(PopupRequest popupRequest, Popup popup, Post post) {
        popup.setTitle(popupRequest.getTitle());
        popup.setContent(popupRequest.getContent());
        popup.setDisplayTargetType(PopupDisplayTargetType.fromValue(popupRequest.getDisplayTargetType()));
        popup.setStockTargetType(PushTargetType.fromValue(popupRequest.getStockTargetType()));
        popup.setStockCode(popupRequest.getStockCode());
        popup.setStockGroupId(popupRequest.getStockGroupId());
        popup.setStatus(Status.fromValue(popupRequest.getStatus()));
        popup.setTargetStartDatetime(DateTimeConverter.convert(popupRequest.getTargetStartDatetime()));
        popup.setTargetEndDatetime(DateTimeConverter.convert(popupRequest.getTargetEndDatetime()));
        popup.setPostId(popupRequest.getPostId());
        popup.setBoardCategory(getBoardCategory(post));
        popup.setLinkType(AppLinkType.fromValue(popupRequest.getLinkType()));
        popup.setLinkTitle(popupRequest.getLinkTitle());
        popup.setLinkUrl(appLinkUrlManager.getLinkToSaveByLinkType(popup, post));
        return popup;
    }

    private BoardCategory getBoardCategory(Post post) {
        if (post == null) {
            return null;
        }
        return post.getBoard().getCategory();
    }
}
