package ag.act.api.admin.post;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Poll;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class PostsByPollDuplicateApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/posts/{postId}/stock-groups/{stockGroupId}/duplicate";
    private final List<Long> targetBoardIds = new ArrayList<>();
    private final List<Long> anotherStockGroupBoardIds = new ArrayList<>();
    private String jwt;
    private Long postId;
    private Post post;
    private PostImage postImage1;
    private PostImage postImage2;
    private Long stockGroupId;
    private PostUserProfile postUserProfile;
    private BoardGroup boardGroup;
    private BoardCategory boardCategory;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());

        final String stockGroupName = someAlphanumericString(10);
        final String targetStockCode = someStockCode();
        final Stock stock = createStock(user, targetStockCode);
        final String anotherStockCode = someStockCode();
        createStock(user, anotherStockCode);

        // 복제할 게시글 정보
        boardGroup = BoardGroup.ACTION;
        boardCategory = BoardCategory.SURVEYS;
        post = itUtil.createPost(itUtil.createBoard(stock, boardGroup, boardCategory), user.getId());
        itUtil.createPoll(post);
        postId = post.getId();
        postImage1 = itUtil.createPostImage(postId);
        postImage2 = itUtil.createPostImage(postId);
        postUserProfile = post.getPostUserProfile();

        // 복제할 게시글의 종목그룹 생성
        final StockGroup stockGroup = itUtil.createStockGroup(stockGroupName);
        stockGroupId = stockGroup.getId();
        itUtil.createStockGroupMapping(targetStockCode, stockGroupId);

        // 다른 종목그릅 생성
        final StockGroup anotherStockGroup = itUtil.createStockGroup(someAlphanumericString(20));
        final Long anotherStockGroupId = anotherStockGroup.getId();
        itUtil.createStockGroupMapping(anotherStockCode, anotherStockGroupId);

        // 복제할 갯수, 테스트를 위한 가짜 갯수 생성
        int targetStockGroupAndMappingCount = someIntegerBetween(2, 10);
        int invalidStockGroupAndMappingCount = someIntegerBetween(2, 10);

        // 복제할 게시글의 종목그룹에 속한 게시판 생성
        for (int i = 0; i < targetStockGroupAndMappingCount; i++) {
            targetBoardIds.add(createStockAndBoardInSameStockGroup(stockGroup));
        }

        // 종목그룹과 매핑은 만들지만 실제 게시판이 없는 가짜 데이터 생성
        for (int i = 0; i < invalidStockGroupAndMappingCount; i++) {
            createStockInSameStockGroup(stockGroup);
            createStockInSameStockGroup(anotherStockGroup);
        }
    }

    private Stock createStock(User user, String targetStockCode) {
        final Stock stock = itUtil.createStock(targetStockCode);
        itUtil.createSolidarity(targetStockCode);
        itUtil.createUserHoldingStock(targetStockCode, user);
        return stock;
    }

    private Long createStockAndBoardInSameStockGroup(StockGroup stockGroup) {
        final String stockCode = someStockCode();
        final Stock stock = itUtil.createStock(stockCode);
        itUtil.createStockGroupMapping(stockCode, stockGroup.getId());
        return itUtil.createBoard(stock, boardGroup, boardCategory).getId();
    }

    private void createStockInSameStockGroup(StockGroup stockGroup) {
        final String stockCode = someStockCode();
        itUtil.createStock(stockCode);
        itUtil.createStockGroupMapping(stockCode, stockGroup.getId());
    }

    private void assertValidBoardPostsFromDatabase(List<Long> boardIds) {
        for (Long boardId : boardIds) {
            final Post duplicatedPost = itUtil.findOnePostByBoardId(boardId).orElseThrow();

            assertThat(duplicatedPost.getTitle(), is(post.getTitle()));
            assertThat(duplicatedPost.getContent(), is(post.getContent()));
            assertThat(duplicatedPost.getAnonymousName(), is(post.getAnonymousName()));
            assertThat(duplicatedPost.getSourcePostId(), is(postId));
            assertThat(duplicatedPost.getStatus(), is(post.getStatus()));
            assertThat(duplicatedPost.getUserId(), is(post.getUserId()));

            final PostUserProfile duplicatedPostUserProfile = duplicatedPost.getPostUserProfile();
            assertThat(duplicatedPostUserProfile.getUserIp(), is(postUserProfile.getUserIp()));
            assertThat(duplicatedPostUserProfile.getNickname(), is(postUserProfile.getNickname()));
            assertThat(duplicatedPostUserProfile.getProfileImageUrl(), is(postUserProfile.getProfileImageUrl()));
            assertThat(duplicatedPostUserProfile.getIndividualStockCountLabel(), is(postUserProfile.getIndividualStockCountLabel()));
            assertThat(duplicatedPostUserProfile.getTotalAssetLabel(), is(postUserProfile.getTotalAssetLabel()));
            assertThat(duplicatedPostUserProfile.getStatus(), is(postUserProfile.getStatus()));

            List<PostImage> duplicatedPostImages = itUtil.findPostImagesByPostId(duplicatedPost.getId());
            assertThat(duplicatedPostImages.size(), is(2));
            assertThat(duplicatedPostImages.get(0).getImageId(), is(postImage1.getImageId()));
            assertThat(duplicatedPostImages.get(1).getImageId(), is(postImage2.getImageId()));

            final Poll afterPoll = duplicatedPost.getFirstPoll();
            final Poll beforePoll = post.getFirstPoll();

            assertThat(afterPoll, is(notNullValue()));
            assertThat(afterPoll.getTitle(), is(beforePoll.getTitle()));
            assertThat(afterPoll.getSelectionOption(), is(beforePoll.getSelectionOption()));
            assertThat(afterPoll.getVoteType(), is(beforePoll.getVoteType()));
            assertTime(afterPoll.getTargetStartDate(), beforePoll.getTargetStartDate());
            assertTime(afterPoll.getTargetEndDate(), beforePoll.getTargetEndDate());

            final List<PollItem> afterPollItemList = afterPoll.getPollItemList();
            final List<PollItem> beforePollItemList = beforePoll.getPollItemList();

            assertThat(afterPollItemList.size(), is(beforePollItemList.size()));

            for (int k = 0; k < afterPollItemList.size(); k++) {
                PollItem afterPollItem = afterPollItemList.get(k);
                PollItem beforePollItem = beforePollItemList.get(k);

                assertThat(afterPollItem.getText(), is(beforePollItem.getText()));
            }
        }
    }

    private void assertEmptyStockGroupBoardPostsFromDatabase(List<Long> stockGroupBoardIds) {
        for (Long anotherStockGroupBoardId : stockGroupBoardIds) {
            final Optional<Post> onePostByBoardId = itUtil.findOnePostByBoardId(anotherStockGroupBoardId);
            assertThat(onePostByBoardId.isEmpty(), is(true));
        }
    }

    private ag.act.model.SimpleStringResponse callApi(Long stockGroupId) throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API, postId, stockGroupId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SimpleStringResponse.class
        );
    }

    @Nested
    class WhenDuplicatePostAndPollSuccess {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccessMessage() throws Exception {
            final ag.act.model.SimpleStringResponse result = callApi(stockGroupId);

            assertThat(result.getStatus(), containsString("초 동안 %s건의 게시글을 복제하였습니다.".formatted(targetBoardIds.size())));
            assertValidBoardPostsFromDatabase(targetBoardIds);
            assertEmptyStockGroupBoardPostsFromDatabase(anotherStockGroupBoardIds);
        }
    }
}
