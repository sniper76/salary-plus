package ag.act.api.stockhome;

import ag.act.converter.user.UserIpConverter;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostUserProfile;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.ReportType;
import ag.act.model.CarouselItemResponse;
import ag.act.model.SectionItemResponse;
import ag.act.model.Status;
import ag.act.model.StockHomeResponse;
import ag.act.model.StockHomeSectionResponse;
import ag.act.module.cache.PostPreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.replacePlaceholders;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;

@SuppressWarnings({"AbbreviationAsWordInName"})
class GetStockHomeSectionsApiIntegrationTest extends GetStockHomeApiIntegrationTest {

    private static final List<BoardGroup> LIST_SECTION_BOARD_GROUPS = List.of(
        BoardGroup.DEBATE,
        BoardGroup.ANALYSIS,
        BoardGroup.ACTION
    );

    @Autowired
    private UserIpConverter userIpConverter;
    @Autowired
    private PostPreference postPreference;
    private PostUserProfile profileOfUser;
    private Boolean isAllNewPost;
    private Boolean isAllExclusiveToHolders;

    @BeforeEach
    void setUp() {
        isAllNewPost = someBoolean();
        isAllExclusiveToHolders = someBoolean();
    }

    @Nested
    class WhenBoardsNotExist {

        @BeforeEach
        void setUp() {
            itUtil.createBoard(stock);
        }

        @DisplayName("Should return 200 response code with empty section" + TARGET_API)
        @Test
        void shouldReturnSuccessWithEmptySections() throws Exception {
            StockHomeResponse result = getResponse(callApi(status().isOk()));
            assertThat(result.getSections().isEmpty(), is(true));
        }
    }

    @Nested
    class WhenAllBoardsAndPostsExists {
        @BeforeEach
        void setUp() {
            BoardCategory[] categories = BoardCategory.activeBoardCategoriesForStocks();
            for (BoardCategory category : categories) {
                final Board board = itUtil.createBoard(stock, category.getBoardGroup(), category);
                IntStream.range(0, 4).forEach(i -> createPost(board));
            }
        }

        @DisplayName("Should return 200 response code, with sections that contain three most recent posts" + TARGET_API)
        @Test
        void shouldReturnSuccessWithRecentThreePosts() throws Exception {
            StockHomeResponse result = getResponse(callApi(status().isOk()));
            assertSectionResponse(result);
        }
    }

    @Nested
    class WhenAllPostsAreDeleted {

        private void assertDeletedContents(ag.act.model.StockHomeResponse result, String expectedTitle) {
            assertSectionContent(result, listItems -> this.assertAllItemsDeleted(expectedTitle, listItems));
        }

        private void assertAllItemsDeleted(String expectedTitle, List<SectionItemResponse> listItems) {
            for (SectionItemResponse listItem : listItems) {
                assertThat(listItem.getTitle(), is(expectedTitle));
                assertThat(listItem.getDeleted(), is(true));
            }
        }

        @Nested
        class AndDeletedByUser {
            @BeforeEach
            void setUp() {
                BoardCategory[] categories = BoardCategory.activeBoardCategoriesForStocks();
                for (BoardCategory category : categories) {
                    final Board board = itUtil.createBoard(stock, category.getBoardGroup(), category);

                    IntStream.range(0, 6).forEach(i -> {
                        final Post post = createPost(board);
                        deletePost(post, ag.act.model.Status.DELETED_BY_USER);
                    });
                }
            }

            @DisplayName("Should return 200 response code, with sections that contain three most recent posts" + TARGET_API)
            @Test
            void shouldReturnSuccessWithRecentThreePosts() throws Exception {
                StockHomeResponse result = getResponse(callApi(status().isOk()));

                assertSectionResponse(result);
                assertDeletedContents(result, "삭제된 게시글입니다.");
            }
        }

        @Nested
        class AndDeletedByAdmin {
            @BeforeEach
            void setUp() {
                BoardCategory[] categories = BoardCategory.activeBoardCategoriesForStocks();
                for (BoardCategory category : categories) {
                    final Board board = itUtil.createBoard(stock, category.getBoardGroup(), category);

                    IntStream.range(0, 6).forEach(i -> {
                        final Post post = createPost(board);
                        deletePost(post, Status.DELETED_BY_ADMIN);
                    });
                }
            }

            @DisplayName("Should return 200 response code, with sections that contain three most recent posts" + TARGET_API)
            @Test
            void shouldReturnSuccessWithRecentThreePosts() throws Exception {
                StockHomeResponse result = getResponse(callApi(status().isOk()));

                assertThat(result.getSections().size(), is(0));
            }
        }
    }

    @Nested
    class WhenAllPostsAreReported {
        @BeforeEach
        void setUp() {
            BoardCategory[] categories = BoardCategory.activeBoardCategoriesForStocks();
            for (BoardCategory category : categories) {
                final Board board = itUtil.createBoard(stock, category.getBoardGroup(), category);

                IntStream.range(0, 3).forEach(i -> {
                    Post post = createPost(board);
                    itUtil.createReport(post.getId(), currentUser, ReportType.POST);
                });
            }
        }

        @Test
        void shouldReturnRecentThreeContentsButIncludingReported() throws Exception {
            StockHomeResponse result = getResponse(callApi(status().isOk()));

            assertSectionResponse(result);
            assertReportedContent(result);
        }

        private void assertReportedContent(ag.act.model.StockHomeResponse result) {
            assertSectionContent(result, this::assertAllItemsReported);
        }

        private void assertAllItemsReported(List<SectionItemResponse> listItems) {
            for (SectionItemResponse listItem : listItems) {
                assertThat(listItem.getTitle(), is("신고된 게시글입니다."));
                assertThat(listItem.getReported(), is(true));
            }
        }
    }

    @Nested
    class WhenPostsAreEditedAtYesterday {
        private LocalDateTime editedAt;

        @BeforeEach
        void setUp() {
            editedAt = LocalDateTime.now().minusDays(1);

            BoardCategory[] categories = BoardCategory.activeBoardCategoriesForStocks();
            for (BoardCategory category : categories) {
                final Board board = itUtil.createBoard(stock, category.getBoardGroup(), category);

                IntStream.range(0, 3).forEach(i -> {
                    Post post = createPost(board);
                    post.setEditedAt(editedAt);
                    itUtil.updatePost(post);
                });
            }
        }

        @Test
        void shouldReturnRecentThreeContentsEditedYesterday() throws Exception {
            StockHomeResponse result = getResponse(callApi(status().isOk()));

            assertSectionResponse(result);
            assertYesterdayContent(result);
        }

        private void assertYesterdayContent(ag.act.model.StockHomeResponse result) {
            assertSectionContent(result, this::assertAllItemsShouldBeUpdatedYesterday);
        }

        private void assertAllItemsShouldBeUpdatedYesterday(List<SectionItemResponse> listItems) {
            for (SectionItemResponse listItem : listItems) {
                assertTime(listItem.getUpdatedAt(), editedAt);
            }
        }
    }

    private Post createPost(Board board) {
        Post post = itUtil.createPost(board, currentUser.getId());
        profileOfUser = post.getPostUserProfile();
        if (!isAllNewPost) {
            post = itUtil.updateCreatedAtOfPost(post, LocalDateTime.now().minusHours(postPreference.getNewPostThresholdHours() + 1));
        }

        if (isAllExclusiveToHolders) {
            post.setIsExclusiveToHolders(true);
            post = itUtil.updatePost(post);
        }
        return post;
    }

    private void deletePost(Post post, ag.act.model.Status status) {
        post.setStatus(status);
        post.setDeletedAt(LocalDateTime.now());
        post.setTitle("DELETED_POST_FOR_TEST_%s_%s_%s".formatted(post.getId(), post.getTitle(), status.name()));
        itUtil.updatePost(post);
    }

    @SuppressWarnings({"ConstantValue"})
    private void assertSectionResponse(ag.act.model.StockHomeResponse result) {
        final List<StockHomeSectionResponse> sections = result.getSections();

        final int carouselSectionCount = List.of().size();
        final int listSectionCount = LIST_SECTION_BOARD_GROUPS.size();

        assertThat(sections.size(), is(carouselSectionCount + listSectionCount));

        sections.stream()
            .limit(carouselSectionCount)
            .forEach(this::assertCarouselSectionResponse);

        int sectionIndex = carouselSectionCount;
        assertListSectionResponse(sections.get(sectionIndex++), "debate", "토론방");
        assertListSectionResponse(sections.get(sectionIndex++), "analysis", "공지");
        assertListSectionResponse(sections.get(sectionIndex), "action", "액션");
    }

    private void assertListSectionResponse(
        StockHomeSectionResponse stockHomeSectionResponse,
        String boardGroupName,
        String sectionTitle
    ) {
        assertThat(stockHomeSectionResponse.getType(), is("list"));
        assertThat(stockHomeSectionResponse.getListItems().size(), is(3));
        assertSectionItemResponse(stockHomeSectionResponse.getListItems());

        assertListSectionHeaderLink(stockHomeSectionResponse, boardGroupName);
        assertListSectionHeaderTitle(stockHomeSectionResponse, sectionTitle);
    }

    private void assertCarouselSectionResponse(StockHomeSectionResponse stockHomeSectionResponse) {
        assertThat(stockHomeSectionResponse.getType(), is("carousel"));
        assertThat(stockHomeSectionResponse.getCarouselItems().get(0).getHeader().getImage(), matchesPattern("test_analysis_header_image_url"));
        assertThat(stockHomeSectionResponse.getCarouselItems().get(1).getHeader().getImage(), matchesPattern("test_debate_header_image_url"));

        assertThat(stockHomeSectionResponse.getCarouselItems().size(), is(2));
        assertThat(stockHomeSectionResponse.getCarouselItems().get(0).getListItems().size(), is(3));
        assertThat(stockHomeSectionResponse.getCarouselItems().get(1).getListItems().size(), is(3));

        assertSectionItemResponse(stockHomeSectionResponse.getCarouselItems().get(0).getListItems());
        assertSectionItemResponse(stockHomeSectionResponse.getCarouselItems().get(1).getListItems());

        assertCarouselSectionHeaderLink(stockHomeSectionResponse);
    }

    private void assertSectionItemResponse(List<SectionItemResponse> sectionItemResponses) {
        assertThat(sectionItemResponses.size(), is(3));

        assertDefaultSectionItemResponse(sectionItemResponses.get(0));
        assertDefaultSectionItemResponse(sectionItemResponses.get(1));
        assertDefaultSectionItemResponse(sectionItemResponses.get(2));
    }

    private void assertDefaultSectionItemResponse(SectionItemResponse sectionItemResponse) {
        assertThat(sectionItemResponse.getViewCount(), notNullValue());
        assertThat(sectionItemResponse.getLikeCount(), notNullValue());
        assertThat(sectionItemResponse.getCommentCount(), notNullValue());
        assertThat(sectionItemResponse.getBoardCategory(), notNullValue());
        assertThat(sectionItemResponse.getLink(), notNullValue());
        assertThat(sectionItemResponse.getTitle(), notNullValue());
        assertThat(sectionItemResponse.getIsNew(), is(isAllNewPost));
        assertThat(sectionItemResponse.getIsExclusiveToHolders(), is(isAllExclusiveToHolders));
        if (sectionItemResponse.getDeleted()) {
            assertThat(sectionItemResponse.getUserProfile(), nullValue());
        } else {
            assertUserProfileResponse(sectionItemResponse.getUserProfile());
        }
    }

    private void assertUserProfileResponse(ag.act.model.UserProfileResponse userProfileResponse) {
        assertThat(userProfileResponse.getNickname(), is(profileOfUser.getNickname()));
        assertThat(userProfileResponse.getIndividualStockCountLabel(), is(profileOfUser.getIndividualStockCountLabel()));
        assertThat(userProfileResponse.getTotalAssetLabel(), is(profileOfUser.getTotalAssetLabel()));
        assertThat(userProfileResponse.getProfileImageUrl(), is(profileOfUser.getProfileImageUrl()));
        assertThat(userProfileResponse.getUserIp(), is(userIpConverter.convert(profileOfUser.getUserIp())));
    }

    private void assertCarouselSectionHeaderLink(StockHomeSectionResponse stockHomeSectionResponse) {
        final String boardGroupUrlTemplate = "/stock/{stockCode}/{boardGroup}";

        final List<CarouselItemResponse> carouselItems = stockHomeSectionResponse.getCarouselItems();

        assertThat(carouselItems.get(0).getHeader().getLink(), is(replacePlaceholders(boardGroupUrlTemplate, stock.getCode(), "analysis")));
        assertThat(carouselItems.get(1).getHeader().getLink(), is(replacePlaceholders(boardGroupUrlTemplate, stock.getCode(), "debate")));

        assertSectionItemLinks(carouselItems.get(0).getListItems(), "analysis");
        assertSectionItemLinks(carouselItems.get(1).getListItems(), "debate");
    }

    private void assertListSectionHeaderLink(StockHomeSectionResponse stockHomeSection, String boardGroupName) {
        final String boardGroupUrlTemplate = "/stock/{stockCode}/{boardGroup}";

        assertThat(stockHomeSection.getHeader().getLink(),
            is(replacePlaceholders(boardGroupUrlTemplate, stock.getCode(), boardGroupName))
        );
        assertSectionItemLinks(stockHomeSection.getListItems(), boardGroupName);
    }

    private void assertListSectionHeaderTitle(StockHomeSectionResponse stockHomeSection, String displayName) {
        assertThat(stockHomeSection.getHeader().getTitle(), is(displayName));
    }

    private void assertSectionItemLinks(List<ag.act.model.SectionItemResponse> listItems, String boardGroupName) {
        final String postHomeUrlTemplate = "/stock/{stockCode}/board/{boardGroup}/post";

        assertThat(listItems.get(0).getLink(), startsWith(replacePlaceholders(postHomeUrlTemplate, stock.getCode(), boardGroupName)));
        assertThat(listItems.get(1).getLink(), startsWith(replacePlaceholders(postHomeUrlTemplate, stock.getCode(), boardGroupName)));
        assertThat(listItems.get(2).getLink(), startsWith(replacePlaceholders(postHomeUrlTemplate, stock.getCode(), boardGroupName)));
    }

    private void assertSectionContent(ag.act.model.StockHomeResponse result, Consumer<List<SectionItemResponse>> validateAction) {
        final List<StockHomeSectionResponse> sections = result.getSections();

        sections.stream()
            .filter(section -> "carousel".equals(section.getType()))
            .forEach(section -> {
                validateAction.accept(section.getCarouselItems().get(0).getListItems());
                validateAction.accept(section.getCarouselItems().get(1).getListItems());
            });

        sections.stream()
            .filter(section -> "list".equals(section.getType()))
            .forEach(section -> validateAction.accept(section.getListItems()));
    }
}
