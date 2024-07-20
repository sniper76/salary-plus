package ag.act.facade;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.PageDataConverter;
import ag.act.converter.home.HomeResponseConverter;
import ag.act.converter.post.PreviewPostResponseConverter;
import ag.act.converter.stock.MySolidarityResponseConverter;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.dto.MySolidarityDto;
import ag.act.dto.SimplePageDto;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.enums.LinkType;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import ag.act.model.HomeLinkResponse;
import ag.act.model.MySolidarityResponse;
import ag.act.service.HomeService;
import ag.act.service.LinkUrlService;
import ag.act.service.notification.NotificationService;
import ag.act.service.post.UnreadPostService;
import ag.act.service.solidarity.MySolidarityService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.service.user.UserService;
import ag.act.service.virtualboard.VirtualBoardGroupPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeFacade {
    private static final int FIRST_PAGE = 1;
    private static final int ACT_BEST_PREVIEW_SIZE = 2;
    private static final List<String> ACT_BEST_SORTS_QUERIES = List.of("createdAt:desc");

    private final HomeResponseConverter homeResponseConverter;
    private final UserService userService;
    private final HomeService homeService;
    private final UserHoldingStockService userHoldingStockService;
    private final MySolidarityResponseConverter mySolidarityResponseConverter;
    private final MySolidarityService mySolidarityService;
    private final LinkUrlService linkUrlService;
    private final NotificationService notificationService;
    private final PageDataConverter pageDataConverter;
    private final PreviewPostResponseConverter previewPostResponseConverter;
    private final UnreadPostService unreadPostService;
    private final VirtualBoardGroupPostService virtualBoardGroupPostService;

    public ag.act.model.HomeResponse getHome() {
        User user = userService.getUser(ActUserProvider.getNoneNull().getId());

        return homeResponseConverter.convert(
            mySolidarityService.getSolidarityResponsesIncludingLinks(user),
            notificationService.getUnreadNotificationsCount(user.getId()),
            getActBestPostPreviews(),
            unreadPostService.getUnreadPostStatus(user.getId())
        );
    }

    private List<ag.act.model.PostResponse> getActBestPostPreviews() {
        final PageRequest pageRequest = pageDataConverter
            .convert(
                FIRST_PAGE,
                ACT_BEST_PREVIEW_SIZE,
                ACT_BEST_SORTS_QUERIES
            );

        final GetBoardGroupPostDto getBoardGroupPostDto = GetBoardGroupPostDto.builder()
            .boardGroupName(VirtualBoardGroup.ACT_BEST.name())
            .build();

        final Page<Post> bestPostPage = virtualBoardGroupPostService.getBestPosts(getBoardGroupPostDto, pageRequest);

        return previewPostResponseConverter.convert(bestPostPage.getContent());
    }

    public SimplePageDto<MySolidarityResponse> getMySolidarityList(Pageable pageable) {
        User user = userService.getUser(ActUserProvider.getNoneNull().getId());

        Page<MySolidarityDto> mySolidarityDtoList = userHoldingStockService.getAllSortedMySolidarityList(
            user.getId(), pageable
        );

        return new SimplePageDto<>(mySolidarityDtoList.map(mySolidarityResponseConverter));
    }

    @Transactional
    public SimplePageDto<MySolidarityResponse> updateMySolidarityListDisplayOrder(
        List<String> stockCodes, Pageable pageable
    ) {
        User user = userService.getUser(ActUserProvider.getNoneNull().getId());

        Page<MySolidarityDto> mySolidarityDtoList = homeService.updateUserHoldingStocks(user, stockCodes, pageable);
        userService.saveUser(user);

        return new SimplePageDto<>(mySolidarityDtoList.map(mySolidarityResponseConverter));
    }

    public HomeLinkResponse getHomeLink(String linkType) {
        return linkUrlService.findByLinkType(LinkType.fromValue(linkType));
    }

    public HomeLinkResponse updateHomeLink(String linkType, String linkUrl) {
        return linkUrlService.updateByLinkType(LinkType.fromValue(linkType), linkUrl);
    }
}
