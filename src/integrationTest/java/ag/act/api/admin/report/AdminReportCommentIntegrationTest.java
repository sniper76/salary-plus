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
import ag.act.model.ReportDetailResponse;
import ag.act.model.ReportResponse;
import ag.act.model.ReportStatus;
import ag.act.model.Status;
import ag.act.model.UpdateReportStatusRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class AdminReportCommentIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/{reportType}/reports/{reportId}";

    private String jwt;
    private Report report;
    private Comment comment;
    private User user;
    private ReportStatus currentReportStatus;
    private ReportStatus changeReportStatus;
    private String processMessage;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        final Stock stock = itUtil.createStock();
        final Board board = itUtil.createBoard(stock);
        final Post post = itUtil.createPost(board, user.getId());
        comment = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
    }

    @SuppressWarnings("SameParameterValue")
    private UpdateReportStatusRequest genRequest(String result, ReportStatus currentReportStatus, ReportStatus changeReportStatus) {
        UpdateReportStatusRequest request = new UpdateReportStatusRequest();
        request.setResult(result);
        request.setCurrentReportStatus(currentReportStatus.name());
        request.setChangeReportStatus(changeReportStatus.name());
        return request;
    }

    private ReportDetailResponse getResultResponse(MvcResult response) throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ReportDetailResponse.class
        );
    }

    private Report afterReport(Long id) {
        final Optional<Report> optionalReport = itUtil.findReportById(id);
        assertThat(optionalReport.isPresent(), is(true));
        return optionalReport.get();
    }

    @SuppressWarnings("SameParameterValue")
    private MvcResult getMvcResult(
        ReportType type,
        Long reportId,
        UpdateReportStatusRequest request,
        ResultMatcher resultMatcher
    ) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, type, reportId)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private MvcResult callApi() throws Exception {
        return getMvcResult(
            ReportType.COMMENT,
            report.getId(),
            genRequest(processMessage, currentReportStatus, changeReportStatus),
            status().isOk()
        );
    }

    private Comment getCommentById() {
        return itUtil.findCommentById(comment.getId()).orElseThrow();
    }

    private void assertResponse(ReportDetailResponse result) {
        final CommentResponse commentResponses = result.getComment();
        final ReportResponse reportResponses = result.getReport();

        assertThat(commentResponses.getId(), is(comment.getId()));
        assertThat(reportResponses.getReportId(), is(report.getId()));
        assertThat(reportResponses.getReportHistoryList().size(), is(1));

        Report afterReport = afterReport(report.getId());
        assertThat(afterReport.getReportStatus(), is(changeReportStatus));
    }

    @Nested
    class WhenChangeReadyToComplete {

        @BeforeEach
        void setUp() {
            report = itUtil.createReport(comment.getId(), user, ReportType.COMMENT);

            currentReportStatus = ReportStatus.READY;
            changeReportStatus = ReportStatus.COMPLETE;
            processMessage = someString(30);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ReportDetailResponse result = getResultResponse(
                callApi()
            );

            assertResponse(result);
            assertThat(getCommentById().getStatus(), is(Status.DELETED_BY_ADMIN));
        }
    }

    @Nested
    class WhenChangeReadyToReject {

        @BeforeEach
        void setUp() {
            report = itUtil.createReport(comment.getId(), user, ReportType.COMMENT);

            currentReportStatus = ReportStatus.READY;
            changeReportStatus = ReportStatus.REJECT;
            processMessage = someString(30);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ReportDetailResponse result = getResultResponse(
                callApi()
            );

            assertResponse(result);
            assertThat(getCommentById().getStatus(), is(Status.ACTIVE));
        }
    }

    @Nested
    class WhenChangeCompleteToReject {

        @BeforeEach
        void setUp() {
            comment.setStatus(Status.DELETED_BY_ADMIN);
            comment = itUtil.updateComment(comment);

            currentReportStatus = ReportStatus.COMPLETE;
            changeReportStatus = ReportStatus.REJECT;

            report = itUtil.createReport(comment.getId(), user, ReportType.COMMENT, currentReportStatus);
            processMessage = someString(30);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ReportDetailResponse result = getResultResponse(
                callApi()
            );

            assertResponse(result);
            assertThat(getCommentById().getStatus(), is(Status.ACTIVE));
        }
    }

    @Nested
    class WhenChangeRejectToComplete {

        @BeforeEach
        void setUp() {
            currentReportStatus = ReportStatus.REJECT;
            changeReportStatus = ReportStatus.COMPLETE;

            report = itUtil.createReport(comment.getId(), user, ReportType.COMMENT, currentReportStatus);
            processMessage = someString(30);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ReportDetailResponse result = getResultResponse(
                callApi()
            );

            assertResponse(result);
            assertThat(getCommentById().getStatus(), is(Status.DELETED_BY_ADMIN));
        }
    }

    @Nested
    class FailToChangeReportStatus {
        @Nested
        class WhenChangeRejectToComplete {

            @BeforeEach
            void setUp() {
                currentReportStatus = someThing(ReportStatus.REJECT, ReportStatus.COMPLETE);
                changeReportStatus = someEnum(ReportStatus.class);

                report = itUtil.createReport(comment.getId(), user, ReportType.COMMENT, ReportStatus.READY);
                processMessage = someString(30);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                final MvcResult mvcResult = getMvcResult(
                    ReportType.COMMENT,
                    report.getId(),
                    genRequest(processMessage, currentReportStatus, changeReportStatus),
                    status().isBadRequest()
                );

                itUtil.assertErrorResponse(mvcResult, 400, "신고된 정보가 이미 변경되었습니다.");
            }
        }
    }
}
