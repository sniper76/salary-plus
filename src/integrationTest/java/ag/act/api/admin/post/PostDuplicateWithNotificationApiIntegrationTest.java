package ag.act.api.admin.post;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.notification.Notification;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.PostCopyRequest;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.SimpleImageResponse;
import ag.act.model.Status;
import ag.act.model.UserProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostDuplicateWithNotificationApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/posts/{postId}/duplicate";
    private String adminJwt;
    private Long postId;
    private Post post;
    private PostImage postImage1;
    private PostImage postImage2;
    private PostUserProfile postUserProfile;
    private BoardGroup boardGroup;
    private BoardCategory boardCategory;
    private Stock stock;
    private Board board;
    private Board newBoard;
    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());

        user = itUtil.createUser();
        final String stockCode = someStockCode();
        stock = createStock(user, stockCode);

        // 복제할 게시글 정보
        boardGroup = BoardGroup.ANALYSIS;
        boardCategory = boardGroup.getCategories().get(0);
        board = itUtil.createBoard(stock, boardGroup, boardCategory);
        post = itUtil.createPost(board, user.getId());
        post.setIsNotification(Boolean.TRUE);
        itUtil.updatePost(post);

        postId = post.getId();
        notification = itUtil.createNotification(postId);

        postImage1 = itUtil.createPostImage(postId);
        postImage2 = itUtil.createPostImage(postId);
        postUserProfile = post.getPostUserProfile();
    }

    private PostCopyRequest getPostCopyRequest(String stockCode) {
        return new PostCopyRequest().stockCode(stockCode);
    }

    private Stock createStock(User user, String targetStockCode) {
        final Stock stock = itUtil.createStock(targetStockCode);
        itUtil.createSolidarity(targetStockCode);
        itUtil.createUserHoldingStock(targetStockCode, user);
        return stock;
    }

    private PostDetailsDataResponse callApi(Long postId, String stockCode) throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API, postId)
                    .content(objectMapperUtil.toRequestBody(getPostCopyRequest(stockCode)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminJwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            PostDetailsDataResponse.class
        );
    }

    private void assertResponse(PostDetailsDataResponse result) {
        final PostDetailsResponse createResponse = result.getData();

        assertThat(createResponse.getPoll(), is(nullValue()));
        assertThat(createResponse.getDigitalProxy(), is(nullValue()));
        assertThat(createResponse.getDigitalDocument(), is(nullValue()));

        assertThat(createResponse.getId(), is(notNullValue()));
        assertThat(createResponse.getBoardId(), is(newBoard.getId()));
        assertThat(createResponse.getTitle(), is(post.getTitle()));
        assertThat(createResponse.getContent(), is(post.getContent()));
        assertThat(createResponse.getStatus(), is(Status.ACTIVE));

        final UserProfileResponse createResponseUserProfile = createResponse.getUserProfile();
        assertThat(createResponseUserProfile.getNickname(), is(postUserProfile.getNickname()));
        assertThat(createResponseUserProfile.getProfileImageUrl(), is(postUserProfile.getProfileImageUrl()));
        assertThat(createResponseUserProfile.getIndividualStockCountLabel(), is(postUserProfile.getIndividualStockCountLabel()));
        assertThat(createResponseUserProfile.getTotalAssetLabel(), is(postUserProfile.getTotalAssetLabel()));

        List<SimpleImageResponse> createResponseImages = createResponse.getPostImageList();
        assertThat(createResponseImages.size(), is(2));
        assertThat(createResponseImages.get(0).getImageId(), is(postImage1.getImageId()));
        assertThat(createResponseImages.get(1).getImageId(), is(postImage2.getImageId()));

        final Optional<Notification> optionalNotification = itUtil.findNotificationByPostId(createResponse.getId());
        assertThat(optionalNotification.isPresent(), is(true));
        assertThat(optionalNotification.get().getType(), is(notification.getType()));
        assertThat(optionalNotification.get().getCategory(), is(notification.getCategory()));
        assertThat(optionalNotification.get().getStatus(), is(notification.getStatus()));
    }

    @Nested
    class WhenDuplicatePostSuccess {
        private String newStockCode;
        private Stock newStock;

        @BeforeEach
        void setUp() {
            newStockCode = someStockCode();

            newStock = itUtil.createStock(newStockCode);
            newBoard = itUtil.createBoard(newStock, boardGroup, boardCategory);
        }

        @Nested
        class AndNormalPost {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccessMessage() throws Exception {
                final PostDetailsDataResponse result = callApi(postId, newStockCode);

                assertResponse(result);
            }
        }
    }
}
