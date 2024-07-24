package ag.act.service.virtualboard;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.virtualboard.VirtualBoardCategory;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import ag.act.model.Status;
import ag.act.service.post.BestPostService;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.util.StatusUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class VirtualBoardGroupPostServiceTest {

    @Mock
    private UserHoldingStockService userHoldingStockService;
    @Mock
    private BestPostService bestPostService;
    @Mock
    private BlockedUserService blockedUserService;
    @Mock
    private VirtualBoardService virtualBoardService;
    @InjectMocks
    private VirtualBoardGroupPostService service;

    @Nested
    class GetBestPostForOnlyExclusiveToHolders {

        @Mock
        private User user;
        @Mock
        private GetBoardGroupPostDto getBoardGroupPostDto;
        @Mock
        private List<BoardCategory> categories;
        @Mock
        private PageRequest pageRequest;
        @Mock
        private Page<Post> pagePost;
        @Mock
        private List<String> userHoldingStockCodes;
        @Mock
        private List<Long> blockedUserIdList;
        private List<MockedStatic<?>> statics;

        private final List<Status> statusList = StatusUtil.getPostStatusesVisibleToUsers();

        @BeforeEach
        void setUp() {
            statics = List.of(mockStatic(VirtualBoardCategory.class), mockStatic(ActUserProvider.class));
            VirtualBoardCategory virtualBoardCategory = VirtualBoardCategory.ACT_BEST_STOCK;
            categories = virtualBoardCategory.getSubCategories();
            final Long userId = someLong();

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);
            given(userHoldingStockService.getAllUserHoldingStockCodes(userId)).willReturn(userHoldingStockCodes);
            given(blockedUserService.getBlockUserIdListOfMine()).willReturn(blockedUserIdList);
            given(VirtualBoardCategory.getBoardCategories(getBoardGroupPostDto.getBoardCategoryNames()))
                .willReturn(categories);
            given(getBoardGroupPostDto.isExclusiveToHolders()).willReturn(true);
            given(bestPostService.getBestPostsForOnlyExclusiveToHolders(
                categories,
                statusList,
                userHoldingStockCodes,
                blockedUserIdList,
                pageRequest
            )).willReturn(pagePost);
        }

        @Test
        void shouldReturnPageObject() {

            final Page<Post> actual = service.getBestPosts(getBoardGroupPostDto, pageRequest);

            assertThat(actual, is(pagePost));
            then(bestPostService).should()
                .getBestPostsForOnlyExclusiveToHolders(categories, statusList, userHoldingStockCodes, blockedUserIdList, pageRequest);
        }

        @AfterEach
        void clean() {
            statics.forEach(MockedStatic::close);
        }
    }

    @Nested
    @DisplayName("액트 베스트의 전체 공개 게시글을")
    class WhenActBestGetOnlyPublic {

        private final VirtualBoardGroup virtualBoardGroup = VirtualBoardGroup.ACT_BEST;

        @Mock
        private Page<Post> pagedPost;
        @Mock
        private List<BoardCategory> categories;
        @Mock
        private PageRequest pageRequest;
        @Mock
        private GetBoardGroupPostDto getBoardGroupPostDto;

        @BeforeEach
        void setUp() {
            given(virtualBoardService.getAllCategoriesUnderVirtualBoardGroup(virtualBoardGroup))
                .willReturn(categories);
            given(bestPostService.getBestPostsForOnlyExclusiveToPublic(anyList(), anyList(), anyList(), any(Pageable.class)))
                .willReturn(pagedPost);
            given(getBoardGroupPostDto.getBoardGroupName()).willReturn(virtualBoardGroup.name());
            given(getBoardGroupPostDto.isExclusiveToHolders()).willReturn(Boolean.FALSE);
            given(getBoardGroupPostDto.getIsExclusiveToPublic()).willReturn(Boolean.TRUE);
            given(getBoardGroupPostDto.getIsNotDeleted()).willReturn(Boolean.TRUE);
        }

        @Test
        @DisplayName("조회할 수 있다.")
        void shouldReturnPageObject() {
            Page<Post> response = service.getBestPosts(getBoardGroupPostDto, pageRequest);

            assertThat(response, is(pagedPost));
        }
    }
}
