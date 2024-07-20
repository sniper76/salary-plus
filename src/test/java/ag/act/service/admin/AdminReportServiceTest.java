package ag.act.service.admin;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.report.AdminReportConverter;
import ag.act.converter.report.AdminReportListResponseConverter;
import ag.act.dto.ReportItemDto;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Report;
import ag.act.entity.User;
import ag.act.enums.ReportType;
import ag.act.repository.AdminReportRepository;
import ag.act.repository.ReportRepository;
import ag.act.service.ReportService;
import ag.act.service.post.PostService;
import ag.act.service.stockboardgrouppost.comment.CommentService;
import ag.act.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class AdminReportServiceTest {
    @InjectMocks
    private ReportService service;
    @Mock
    private PageRequest pageRequest;
    @Mock
    private AdminReportRepository adminReportRepository;
    @Mock
    private AdminReportListResponseConverter adminReportListResponseConverter;
    @Mock
    private ag.act.model.GetReportResponse reportResponse;
    @Mock
    private Report report;
    @Mock
    private User user;
    @Mock
    private Post post;
    @Mock
    private Comment comment;
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private UserService userService;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private AdminReportConverter adminReportConverter;
    @Mock
    private ag.act.model.ReportDetailResponse reportDetailResponse;
    @Mock
    private List<Comment> replyList;

    private Long reportId;
    private Long contentId;
    private ReportType reportType;
    private ag.act.model.ReportStatus reportStatus;
    private List<ReportItemDto> reportItemDtoList;

    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
        reportItemDtoList = List.of(mock(ReportItemDto.class), mock(ReportItemDto.class), mock(ReportItemDto.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenGetPostReports {
        @BeforeEach
        void setUp() {
            reportItemDtoList = List.of(mock(ReportItemDto.class), mock(ReportItemDto.class));
            pageRequest = PageRequest.of(1, reportItemDtoList.size());
            // Given
            final long totalElements = reportItemDtoList.size();

            reportType = ReportType.POST;
            reportStatus = someEnum(ag.act.model.ReportStatus.class);

            // Given
            given(adminReportRepository.countByReport(reportType, reportStatus))
                .willReturn(totalElements);
            given(adminReportRepository.findByReportPosts(
                reportType, reportStatus, pageRequest.getPageSize(), pageRequest.getPageNumber()
            )).willReturn(reportItemDtoList);

            Page<ReportItemDto> reportPage = new PageImpl<>(reportItemDtoList, pageRequest, totalElements);
            given(adminReportListResponseConverter.convert(reportPage))
                .willReturn(reportResponse);
        }

        @Test
        void shouldGetReportsPost() {

            // When
            ag.act.model.GetReportResponse actual = service.getAdminReports(reportType.name(), reportStatus.name(), pageRequest);

            // Then
            assertThat(actual, notNullValue());
            assertThat(actual, is(reportResponse));
            assertThat(actual.getData().size(), is(reportResponse.getData().size()));
        }
    }

    @Nested
    class WhenGetCommentReports {
        @BeforeEach
        void setUp() {
            reportItemDtoList = List.of(mock(ReportItemDto.class), mock(ReportItemDto.class));
            pageRequest = PageRequest.of(1, reportItemDtoList.size());
            // Given
            final long totalElements = reportItemDtoList.size();

            reportType = ReportType.COMMENT;
            reportStatus = someEnum(ag.act.model.ReportStatus.class);

            // Given
            given(adminReportRepository.countByReport(reportType, reportStatus))
                .willReturn(totalElements);
            given(adminReportRepository.findByReportComments(
                reportType, reportStatus, pageRequest.getPageSize(), pageRequest.getPageNumber()
            )).willReturn(reportItemDtoList);

            Page<ReportItemDto> reportPage = new PageImpl<>(reportItemDtoList, pageRequest, totalElements);
            given(adminReportListResponseConverter.convert(reportPage))
                .willReturn(reportResponse);
        }

        @Test
        void shouldGetReportsComment() {

            // When
            ag.act.model.GetReportResponse actual = service.getAdminReports(reportType.name(), reportStatus.name(), pageRequest);

            // Then
            assertThat(actual, notNullValue());
            assertThat(actual, is(reportResponse));
            assertThat(actual.getData().size(), is(reportResponse.getData().size()));
        }
    }

    @Nested
    class WhenGetReportPostDetail {
        @BeforeEach
        void setUp() {
            reportType = ReportType.POST;
            reportStatus = someEnum(ag.act.model.ReportStatus.class);

            reportId = someLong();
            contentId = someLong();

            final Optional<Report> optionalReport = Optional.of(report);
            final Optional<User> optionalUser = Optional.of(user);
            final Optional<Post> optionalPost = Optional.of(post);

            // Given
            given(reportRepository.findById(reportId)).willReturn(optionalReport);
            given(userService.findUser(report.getUserId())).willReturn(optionalUser);
            given(report.getContentId()).willReturn(contentId);
            given(postService.findById(report.getContentId())).willReturn(optionalPost);
            given(adminReportConverter.convertResponse(
                post, user, report
            )).willReturn(reportDetailResponse);
        }

        @Test
        void shouldGetReportPostDetail() {

            // When
            ag.act.model.ReportDetailResponse actual = service.getAdminReportDetail(reportType.name(), reportId);

            // Then
            assertThat(actual, notNullValue());
            assertThat(actual, is(reportDetailResponse));
            assertThat(actual.getPost(), is(reportDetailResponse.getPost()));
            assertThat(actual.getReport(), is(reportDetailResponse.getReport()));
        }
    }

    @Nested
    class WhenGetReportCommentDetail {
        @BeforeEach
        void setUp() {
            reportType = ReportType.COMMENT;
            reportStatus = someEnum(ag.act.model.ReportStatus.class);

            reportId = someLong();
            contentId = someLong();

            final Optional<Report> optionalReport = Optional.of(report);
            final Optional<User> optionalUser = Optional.of(user);
            final Optional<Comment> optionalComment = Optional.of(comment);

            // Given
            given(reportRepository.findById(reportId)).willReturn(optionalReport);
            given(userService.findUser(report.getUserId())).willReturn(optionalUser);
            given(report.getContentId()).willReturn(contentId);
            given(commentService.findById(report.getContentId())).willReturn(optionalComment);
            given(comment.getParentId()).willReturn(contentId);
            given(comment.getId()).willReturn(contentId);
            given(commentService.findAllByParentId(comment.getId())).willReturn(replyList);
            given(adminReportConverter.convertResponse(
                comment, user, replyList, report
            )).willReturn(reportDetailResponse);
        }

        @Test
        void shouldGetReportCommentDetail() {

            // When
            ag.act.model.ReportDetailResponse actual = service.getAdminReportDetail(reportType.name(), reportId);

            // Then
            assertThat(actual, notNullValue());
            assertThat(actual, is(reportDetailResponse));
            assertThat(actual.getComment(), is(reportDetailResponse.getComment()));
            assertThat(actual.getReport(), is(reportDetailResponse.getReport()));
        }
    }
}