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
import ag.act.itutil.holder.ActEntityTestHolder;
import ag.act.model.GetReportResponse;
import ag.act.model.Paging;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminReportApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/{reportType}/reports";
    private static final String TARGET_API_PARAM = "/api/admin/{reportType}/reports?page={page}&size={size}";
    public static final int SIZE_5_PER_PAGE = 5;
    public static final int SIZE_10_PER_PAGE = 10;

    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        post = itUtil.createPost(board, user.getId());
    }

    @Nested
    class WhenCreateReportPosts {
        private Report report;
        private final ActEntityTestHolder<Report, Long> reportsTestHolder = new ActEntityTestHolder<>();

        @BeforeEach
        void setUp() {
            reportsTestHolder.initialize(itUtil.findAllReportsByType(ReportType.POST));

            report = itUtil.createReport(post.getId(), user, ReportType.POST);
            reportsTestHolder.addOrSet(report);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    get(TARGET_API, ReportType.POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final GetReportResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                GetReportResponse.class
            );

            assertResponse(result);
        }

        private void assertResponse(GetReportResponse result) {
            final Paging paging = result.getPaging();
            final List<ag.act.model.ReportListResponse> postResponses = result.getData();

            assertPaging(paging);
            assertPosts(postResponses);
        }

        private void assertPosts(List<ag.act.model.ReportListResponse> postResponses) {
            assertThat(postResponses.size(), is(1));

            final ag.act.model.ReportListResponse postResponse = postResponses.get(0);
            assertThat(postResponse.getTitle(), is(post.getTitle()));
            assertThat(postResponse.getContentId(), is(report.getContentId()));
            assertThat(postResponse.getStockCode(), is(stock.getCode()));
            assertThat(postResponse.getStockName(), is(stock.getName()));
            assertThat(postResponse.getBoardCategoryName(), is(board.getCategory().name()));
            assertThat(postResponse.getBoardGroupName(), is(board.getGroup().name()));
            assertThat(postResponse.getReportStatus(), is(report.getReportStatus().name()));
        }

        private void assertPaging(Paging paging) {
            assertThat(paging.getPage(), is(1));
            final int totalElements = reportsTestHolder.getItems().size();

            assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements * 1.0 / SIZE_10_PER_PAGE)));
            assertThat(paging.getTotalElements(), is((long) totalElements));
        }
    }

    @Nested
    class WhenCreateReportComments {

        private final ActEntityTestHolder<Report, Long> reportsTestHolder = new ActEntityTestHolder<>();

        @BeforeEach
        void setUp() {
            reportsTestHolder.initialize(itUtil.findAllReportsByType(ReportType.COMMENT));

            final Comment comment1 = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
            final Comment comment2 = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
            final Comment comment3 = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
            final Comment comment4 = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
            final Comment comment5 = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
            final Comment comment6 = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);
            final Comment comment7 = itUtil.createComment(post.getId(), user, CommentType.POST, Status.ACTIVE);

            reportsTestHolder.addOrSet(itUtil.createReport(comment1.getId(), user, ReportType.COMMENT));
            reportsTestHolder.addOrSet(itUtil.createReport(comment2.getId(), user, ReportType.COMMENT));
            reportsTestHolder.addOrSet(itUtil.createReport(comment3.getId(), user, ReportType.COMMENT));
            reportsTestHolder.addOrSet(itUtil.createReport(comment4.getId(), user, ReportType.COMMENT));
            reportsTestHolder.addOrSet(itUtil.createReport(comment5.getId(), user, ReportType.COMMENT));
            reportsTestHolder.addOrSet(itUtil.createReport(comment6.getId(), user, ReportType.COMMENT));
            reportsTestHolder.addOrSet(itUtil.createReport(comment7.getId(), user, ReportType.COMMENT));
        }

        @Nested
        class WhenFirstPage {
            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        get(TARGET_API_PARAM, ReportType.COMMENT, 1, SIZE_5_PER_PAGE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

                final GetReportResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    GetReportResponse.class
                );

                final Paging paging = result.getPaging();
                assertThat(paging.getPage(), is(1));

                final int totalElements = reportsTestHolder.getItems().size();
                assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements * 1.0 / SIZE_5_PER_PAGE)));
                assertThat(paging.getTotalElements(), is((long) totalElements));
            }
        }

        @Nested
        class WhenLastPage {
            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        get(TARGET_API_PARAM, ReportType.COMMENT, 2, SIZE_5_PER_PAGE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

                final GetReportResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    GetReportResponse.class
                );

                final Paging paging = result.getPaging();
                assertThat(paging.getPage(), is(2));

                final int totalElements = reportsTestHolder.getItems().size();
                assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements * 1.0 / SIZE_5_PER_PAGE)));
                assertThat(paging.getTotalElements(), is((long) totalElements));
            }
        }
    }
}
