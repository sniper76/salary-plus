package ag.act.api.admin.post.notification;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.notification.Notification;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.RoleType;
import ag.act.enums.notification.NotificationCategory;
import ag.act.enums.notification.NotificationType;
import ag.act.model.CreatePostRequest;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.Status;
import ag.act.model.UpdatePostRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class PostNotificationApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroup}/posts";
    private static final String TARGET_UPDATE_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroup}/posts/{postId}";

    private CreatePostRequest createPostRequest;
    private UpdatePostRequest updatePostRequest;
    private String stockCode;
    private String jwt;
    private Board board;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stockCode = someStockCode();
        Stock stock = itUtil.createStock(stockCode);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);

        board = itUtil.createBoard(stock, BoardGroup.GLOBALBOARD, BoardCategory.STOCKHOLDER_ACTION);
        user = itUtil.createUserRole(user, RoleType.ADMIN);
    }

    private CreatePostRequest genCreateRequest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setBoardCategory(board.getCategory().name());
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsActive(Boolean.TRUE);
        request.setIsNotification(Boolean.TRUE);

        return request;
    }

    private UpdatePostRequest genUpdateRequest(boolean isNotification) {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setBoardCategory(board.getCategory().name());
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsAnonymous(Boolean.FALSE);
        request.setIsActive(Boolean.TRUE);
        request.setIsNotification(isNotification);

        return request;
    }

    @Nested
    class WhenCreatePost {
        @BeforeEach
        void setUp() {
            createPostRequest = genCreateRequest();
        }

        @Nested
        class WhenIsNotificationTrue {
            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        post(TARGET_API, stockCode, board.getGroup().name())
                            .content(objectMapperUtil.toRequestBody(createPostRequest))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

                final PostDetailsDataResponse result = itUtil.getResult(response, PostDetailsDataResponse.class);

                final PostDetailsResponse postDetailsResponse = result.getData();
                assertThat(postDetailsResponse.getIsNotification(), is(createPostRequest.getIsNotification()));
                assertNotification(postDetailsResponse, createPostRequest.getIsNotification());
            }
        }
    }

    private void assertNotification(PostDetailsResponse postDetailsResponse, Boolean isNotification) {
        final Notification notification = itUtil.findNotificationByPostId(postDetailsResponse.getId()).orElseThrow();

        assertThat(notification.getStatus(), is(isNotification ? Status.ACTIVE : Status.DELETED));
        assertTime(postDetailsResponse.getActiveStartDate(), notification.getActiveStartDate());
    }

    @Nested
    class WhenUpdatePost {
        private Long postId;

        @Nested
        class WhenOriginalIsNotificationOfPostIsFalse {
            @BeforeEach
            void setUp() {
                final Post post = itUtil.createPost(board, user.getId(), false);
                post.setIsNotification(false);
                itUtil.updatePost(post);
                postId = post.getId();
            }

            @Nested
            class AndSetIsNotificationTrue {
                @BeforeEach
                void setUp() {
                    updatePostRequest = genUpdateRequest(true);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final PostDetailsDataResponse result = getPostDetailsDataResponseAfterUpdate();
                    final PostDetailsResponse postDetailsResponse = result.getData();

                    assertThat(postDetailsResponse.getIsNotification(), is(updatePostRequest.getIsNotification()));
                    assertNotification(postDetailsResponse, updatePostRequest.getIsNotification());
                }
            }
        }

        @Nested
        class WhenOriginalIsNotificationOfPostIsTrue {
            @BeforeEach
            void setUp() {
                final Post post = itUtil.createPost(board, user.getId(), false);
                post.setIsNotification(true);
                itUtil.updatePost(post);
                postId = post.getId();
            }

            @Nested
            class AndSetIsNotificationFalse {
                @BeforeEach
                void setUp() {
                    updatePostRequest = genUpdateRequest(false);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final PostDetailsDataResponse result = getPostDetailsDataResponseAfterUpdate();
                    final PostDetailsResponse postDetailsResponse = result.getData();

                    assertThat(postDetailsResponse.getIsNotification(), is(updatePostRequest.getIsNotification()));
                    assertNotification(postDetailsResponse, updatePostRequest.getIsNotification());
                }
            }
        }

        @Nested
        class WhenIsNotificationIsNotChanged {

            private Notification notification;

            @BeforeEach
            void setUp() {
                final boolean isNotification = false;
                final Post post = itUtil.createPost(board, user.getId(), false);
                post.setIsNotification(isNotification);
                itUtil.updatePost(post);
                postId = post.getId();
                notification = itUtil.createNotification(postId, NotificationCategory.STOCKHOLDER_ACTION, NotificationType.POST);

                updatePostRequest = genUpdateRequest(isNotification);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final PostDetailsDataResponse result = getPostDetailsDataResponseAfterUpdate();

                assertThat(result.getData().getIsNotification(), is(updatePostRequest.getIsNotification()));

                Notification optinalNotification = itUtil.findNotificationByPostId(postId)
                    .orElseThrow();

                assertThat(optinalNotification.getStatus(), is(notification.getStatus()));
            }
        }

        private PostDetailsDataResponse getPostDetailsDataResponseAfterUpdate() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_UPDATE_API, stockCode, board.getGroup().name(), postId)
                        .content(objectMapperUtil.toRequestBody(updatePostRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            return itUtil.getResult(response, PostDetailsDataResponse.class);
        }
    }
}
