package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.report.ReportConverter;
import ag.act.entity.Report;
import ag.act.entity.User;
import ag.act.enums.ReportType;
import ag.act.model.ReportStatus;
import ag.act.repository.ReportRepository;
import ag.act.validator.ReportValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class ReportServiceTest {
    @InjectMocks
    private ReportService service;

    @Mock
    private ReportValidator reportValidator;
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private ReportConverter reportConverter;

    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
        willDoNothing().given(reportValidator).validate(any(ReportType.class), any(Long.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenCreateReportPost {
        @Mock
        private ag.act.model.ReportContentRequest reportContentRequest;
        @Mock
        private Report report;
        @Mock
        private User user;

        private Long contentId;

        @BeforeEach
        void setUp() {
            contentId = someLong();
            final Long userId = someLong();
            final String reason = someString(10);

            given(reportContentRequest.getReason()).willReturn(reason);

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);

            given(reportConverter.convert(userId, contentId, reportContentRequest.getReason(), ReportType.POST, ReportStatus.READY))
                .willReturn(report);
            given(reportRepository.save(report)).willReturn(report);

        }

        @Test
        void shouldBeCreatePortReport() {
            ag.act.model.SimpleStringResponse okResponse = service.reportBoardGroupPostAndComment(
                contentId, ReportType.POST, reportContentRequest
            );

            assertThat(okResponse.getStatus(), is("ok"));
        }
    }

    @Nested
    class WhenCreateReportComment {
        @Mock
        private ag.act.model.ReportContentRequest reportContentRequest;
        @Mock
        private Report report;
        @Mock
        private User user;

        private Long contentId;

        @BeforeEach
        void setUp() {
            contentId = someLong();
            final Long userId = someLong();
            final String reason = someString(10);

            given(reportContentRequest.getReason()).willReturn(reason);

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);

            given(reportConverter.convert(userId, contentId, reportContentRequest.getReason(), ReportType.COMMENT, ReportStatus.READY))
                .willReturn(report);
            given(reportRepository.save(report)).willReturn(report);

        }

        @Test
        void shouldBeCreateCommentReport() {
            ag.act.model.SimpleStringResponse okResponse = service.reportBoardGroupPostAndComment(
                contentId, ReportType.COMMENT, reportContentRequest
            );

            assertThat(okResponse.getStatus(), is("ok"));
        }
    }

    @Nested
    class GetReportedPostMapByMe {
        @Mock
        private User user;
        @Mock
        private Report report;
        private Long contentId;

        @BeforeEach
        void setUp() {
            final Long userId = someLong();
            contentId = someLong();

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);
            given(reportRepository.findAllByTypeAndUserId(ReportType.POST, userId)).willReturn(List.of(report));
            given(report.getContentId()).willReturn(contentId);
        }

        @Test
        void shouldReturnReportedPostMapByMe() {

            // When
            final Map<Long, Report> reportedPostMapByMe = service.getReportedPostMapByMe();

            // Then
            assertThat(reportedPostMapByMe.get(contentId), is(report));
        }
    }

    @Nested
    class GetReportedCommentMapByMe {
        @Mock
        private User user;
        @Mock
        private Report report;
        private Long contentId;

        @BeforeEach
        void setUp() {
            final Long userId = someLong();
            contentId = someLong();

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);
            given(reportRepository.findAllByTypeAndUserId(ReportType.COMMENT, userId)).willReturn(List.of(report));
            given(report.getContentId()).willReturn(contentId);
        }

        @Test
        void shouldReturnReportedCommentMapByMe() {

            // When
            final Map<Long, Report> reportedPostMapByMe = service.getReportedCommentMapByMe();

            // Then
            assertThat(reportedPostMapByMe.get(contentId), is(report));
        }
    }
}
