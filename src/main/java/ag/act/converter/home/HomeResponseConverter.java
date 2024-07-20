package ag.act.converter.home;

import ag.act.util.AppLinkUrlManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HomeResponseConverter {
    public ag.act.model.HomeResponse convert(
        List<ag.act.model.MySolidarityResponse> solidarityResponses,
        long unreadNotificationsCount,
        List<ag.act.model.PostResponse> actBestPostPreviews,
        ag.act.model.UnreadPostStatus unreadPostStatus
    ) {
        return new ag.act.model.HomeResponse()
            .mySolidarity(solidarityResponses)
            .unreadNotificationsCount(unreadNotificationsCount)
            .actBestPosts(
                new ag.act.model.ActBestPosts()
                    .link(AppLinkUrlManager.ACT_BEST_URL)
                    .posts(actBestPostPreviews)
            )
            .unreadPostStatus(unreadPostStatus);
    }
}
