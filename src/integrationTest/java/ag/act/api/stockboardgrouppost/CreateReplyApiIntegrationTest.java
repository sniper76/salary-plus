package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.TestUtil;
import ag.act.entity.Board;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.CommentType;
import ag.act.model.SimpleStockResponse;
import ag.act.model.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static ag.act.TestUtil.someHtmlContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;

class CreateReplyApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/comments/{commentId}/replies";

    private ag.act.model.CreateCommentRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private Post post;
    private Comment comment;
    private Solidarity solidarity;
    private Boolean isSolidarityLeader;
    private TestUtil.TestHtmlContent testHtmlContent;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUserBeforePinRegistered();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        post = itUtil.createPost(board, user.getId());
        comment = itUtil.createComment(post.getId(), user, CommentType.REPLY_COMMENT, Status.ACTIVE);
        solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private ag.act.model.CreateCommentRequest genRequest(Boolean isAnonymous) {
        ag.act.model.CreateCommentRequest request = new ag.act.model.CreateCommentRequest();
        testHtmlContent = someHtmlContent();
        request.setContent(testHtmlContent.html());
        request.setIsAnonymous(isAnonymous);

        return request;
    }

    @Nested
    class WhenCreateReply {

        private Stock leadingStock1;
        private Stock leadingStock2;
        private Stock leadingStock3;

        @Nested
        class ByNotSolidarityLeader {
            @BeforeEach
            void setUp() {
                request = genRequest(someBoolean());
                isSolidarityLeader = false;
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.CommentDataResponse result = getResponse(callApi(status().isOk()));
                assertResponse(result);
            }
        }

        @Nested
        class BySolidarityLeader {

            @BeforeEach
            void setUp() {
                request = genRequest(false);
                itUtil.createSolidarityLeader(solidarity, user.getId());
                leadingStock1 = createSolidarityLeader(user);
                leadingStock2 = createSolidarityLeader(user);
                leadingStock3 = createSolidarityLeader(user);
                isSolidarityLeader = true;
            }

            private Stock createSolidarityLeader(User user) {
                final Stock stock1 = itUtil.createStock();
                final Solidarity solidarity1 = itUtil.createSolidarity(stock1.getCode());
                itUtil.createSolidarityLeader(solidarity1, user.getId());
                return stock1;
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk());
                assertResponse(getResponse(response));
            }
        }

        @Nested
        class WhenIsAnonymousIsNull {
            @BeforeEach
            void setUp() {
                request = genRequest(null);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "익명여부를 확인해주세요.");
            }
        }

        private void assertResponse(ag.act.model.CommentDataResponse result) {
            final ag.act.model.CommentResponse createUpdateResponse = result.getData();

            assertThat(createUpdateResponse.getId(), is(notNullValue()));
            assertThat(createUpdateResponse.getContent(), is(testHtmlContent.expectedHtml()));
            assertThat(createUpdateResponse.getLikeCount(), is(0L));
            assertThat(createUpdateResponse.getLiked(), is(false));
            assertThat(createUpdateResponse.getDeleted(), is(false));
            assertThat(createUpdateResponse.getReplyComments(), nullValue());
            assertThat(createUpdateResponse.getReplyCommentCount(), nullValue());

            if (request.getIsAnonymous()) {
                assertThat(createUpdateResponse.getUserProfile().getNickname(), is(containsString("익명")));
            } else {
                assertThat(createUpdateResponse.getUserProfile().getNickname(), is(user.getNickname()));
                assertThat(createUpdateResponse.getUserProfile().getProfileImageUrl(), is(user.getProfileImageUrl()));
                assertThat(createUpdateResponse.getUserProfile().getIndividualStockCountLabel(), is("1주+"));
                assertThat(createUpdateResponse.getUserProfile().getTotalAssetLabel(), is(nullValue()));
                assertThat(createUpdateResponse.getUserProfile().getIsSolidarityLeader(), is(isSolidarityLeader));
                if (isSolidarityLeader) {
                    final List<String> leadingStockCodes = createUpdateResponse.getUserProfile()
                        .getLeadingStocks().stream()
                        .map(SimpleStockResponse::getCode)
                        .toList();
                    assertThat(
                        leadingStockCodes,
                        contains(stock.getCode(), leadingStock1.getCode(), leadingStock2.getCode(), leadingStock3.getCode())
                    );
                }
            }
            assertThat(createUpdateResponse.getUserProfile().getUserIp(), is("127.0"));
        }

    }

    private ag.act.model.CommentDataResponse getResponse(MvcResult response) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.CommentDataResponse.class
        );
    }

    @NotNull
    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup(), post.getId(), comment.getId())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
