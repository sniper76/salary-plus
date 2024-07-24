package ag.act.api.admin.report;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Report;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.CommentType;
import ag.act.enums.ReportType;
import ag.act.model.CommentResponse;
import ag.act.model.PostResponse;
import ag.act.model.ReportDetailResponse;
import ag.act.model.ReportResponse;
import ag.act.model.ReportStatus;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminReportDetailApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/{reportType}/reports/{reportId}";

    private String jwt;
    private Board board;
    private Report report;
    private Post post;
    private User user;
    private Comment comment;
    private Comment replyComment;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        Stock stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
    }

    @Nested
    class WhenReportPostDetail {

        @BeforeEach
        void setUp() {
            post = itUtil.createPost(board, user.getId());
            report = itUtil.createReportAndHistory(post.getId(), user, ReportType.POST, ReportStatus.READY);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    get(TARGET_API, ReportType.POST, report.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            final ReportDetailResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ReportDetailResponse.class
            );

            assertResponse(result);
        }

        private void assertResponse(ReportDetailResponse result) {
            final PostResponse postResponses = result.getPost();
            final ReportResponse reportResponses = result.getReport();

            assertThat(postResponses.getId(), is(post.getId()));
            assertThat(reportResponses.getReportId(), is(report.getId()));
            assertThat(reportResponses.getReportHistoryList().size(), is(report.getReportHistoryList().size()));
        }
    }

    @Nested
    class WhenReportReplyCommentDetail {

        @BeforeEach
        void setUp() {
            post = itUtil.createPost(board, user.getId());

            comment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
            replyComment = itUtil.createComment(post.getId(), user.getId(), comment.getId(), CommentType.REPLY_COMMENT, Status.ACTIVE);

            report = itUtil.createReportAndHistory(replyComment.getId(), user, ReportType.COMMENT, ReportStatus.READY);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    get(TARGET_API, ReportType.COMMENT, report.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            final ReportDetailResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ReportDetailResponse.class
            );

            assertResponse(result);
        }

        private void assertResponse(ReportDetailResponse result) {
            final ReportResponse reportResponses = result.getReport();
            final CommentResponse commentResponse = result.getComment();
            final List<CommentResponse> replyList = result.getReply();

            assertThat(commentResponse.getId(), is(comment.getId()));
            assertThat(reportResponses.getReportId(), is(report.getId()));
            assertThat(reportResponses.getReportHistoryList().size(), is(report.getReportHistoryList().size()));

            for (CommentResponse reply : replyList) {
                assertThat(reply.getId(), is(replyComment.getId()));
            }
        }
    }

}
