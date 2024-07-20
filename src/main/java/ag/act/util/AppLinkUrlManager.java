package ag.act.util;

import ag.act.entity.Post;
import ag.act.entity.UserAlert;
import ag.act.enums.AppLinkType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppLinkUrlManager {
    public static final String NOTIFICATION_URL = "/notification";
    public static final String GLOBALBOARD_URL = "/globalboard";
    public static final String MAIN_HOME_URL = "/main";
    public static final String ACT_BEST_URL = "/act-best";
    public static final String DIGITAL_DOCUMENT_URL = "/digitaldocument";

    private final AppLinkUrlGenerator appLinkUrlGenerator;

    public String getLinkToSaveByLinkType(UserAlert userAlert, Post post) {
        AppLinkType appLinkType = userAlert.getLinkType();

        return switch (appLinkType) {
            case NONE -> null;
            case NOTIFICATION -> NOTIFICATION_URL;
            case MAIN_HOME -> MAIN_HOME_URL;
            case STOCK_HOME -> appLinkUrlGenerator.generateStockHomeLinkUrl(userAlert.getStockCode());
            case NEWS_HOME -> GLOBALBOARD_URL;
            case DIGITAL_DOCUMENT_HOME -> DIGITAL_DOCUMENT_URL;
            case LINK -> appLinkUrlGenerator.generateBoardGroupPostLinkUrl(post);
        };
    }

}
