package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.report.AdminReportConverter;
import ag.act.converter.report.AdminReportListResponseConverter;
import ag.act.converter.report.ReportConverter;
import ag.act.dto.ReportItemDto;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Report;
import ag.act.entity.ReportHistory;
import ag.act.entity.User;
import ag.act.enums.ReportType;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.model.ReportDetailResponse;
import ag.act.model.ReportStatus;
import ag.act.model.UpdateReportStatusRequest;
import ag.act.repository.AdminReportRepository;
import ag.act.repository.ReportRepository;
import ag.act.service.post.PostService;
import ag.act.service.stockboardgrouppost.comment.CommentService;
import ag.act.service.user.UserService;
import ag.act.validator.ReportValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {
    private static final String ALL_REPORT_STATUS = "ALL";
    private final ReportRepository reportRepository;
    private final ReportConverter reportConverter;
    private final ReportValidator reportValidator;
    private final AdminReportRepository adminReportRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final AdminReportConverter adminReportConverter;
    private final AdminReportListResponseConverter adminReportListResponseConverter;
    private final UserService userService;

    public ag.act.model.SimpleStringResponse reportBoardGroupPostAndComment(
        Long contentId, ReportType type,
        ag.act.model.ReportContentRequest reportContentRequest
    ) {
        reportValidator.validate(type, contentId);
        reportRepository.save(
            createReport(
                ActUserProvider.getNoneNull().getId(), contentId, type, reportContentRequest
            )
        );

        return new ag.act.model.SimpleStringResponse().status("ok");
    }

    private Report createReport(
        Long userId, Long contentId, ReportType type,
        ag.act.model.ReportContentRequest reportContentRequest
    ) {
        return reportConverter.convert(userId, contentId, reportContentRequest.getReason(), type, ReportStatus.READY);
    }

    public Optional<Report> findByContentId(Long id) {
        return reportRepository.findByContentId(id);
    }

    public Map<Long, Report> getReportedPostMapByMe() {
        return getReportedContentMapByMeAndType(ReportType.POST);
    }

    public Map<Long, Report> getReportedCommentMapByMe() {
        return getReportedContentMapByMeAndType(ReportType.COMMENT);
    }

    private Map<Long, Report> getReportedContentMapByMeAndType(ReportType reportType) {
        final User user = ActUserProvider.getNoneNull();
        return reportRepository.findAllByTypeAndUserId(reportType, user.getId())
            .stream()
            .collect(Collectors.toMap(Report::getContentId, Function.identity(), (v1, v2) -> v1));
    }

    public ag.act.model.GetReportResponse getAdminReports(
        String reportType, String reportStatus, PageRequest pageRequest
    ) {
        ReportType type = ReportType.fromValue(reportType);
        ReportStatus status = null;
        if (reportStatus != null && !ALL_REPORT_STATUS.equals(reportStatus)) {
            status = getReportStatus(reportStatus);
        }

        Page<ReportItemDto> reportPage;
        if (ReportType.POST == type) {
            reportPage = getReportPostList(type, status, pageRequest);
        } else {
            reportPage = getReportCommentList(type, status, pageRequest);
        }
        return adminReportListResponseConverter.convert(reportPage);
    }

    public ReportDetailResponse getAdminReportDetail(
        String reportType, Long reportId
    ) {
        final ReportType type = ReportType.fromValue(reportType);
        final Report report = getReport(reportId);
        final User reportUser = getReportUser(report.getUserId());

        if (ReportType.POST == type) {
            return adminReportConverter.convertResponse(
                getReportedPost(report.getContentId()),
                reportUser,
                report
            );
        }

        final Comment comment = getReportedComment(report.getContentId());
        final List<Comment> replyComments = commentService.findAllByParentId(comment.getId());

        return adminReportConverter.convertResponse(comment, reportUser, replyComments, report);
    }

    private Comment getReportedComment(Long contentId) {
        final Comment comment = commentService.findById(contentId)
            .orElseThrow(() -> new NotFoundException("신고된 댓글 정보가 없습니다."));

        if (comment.getParentId() != null) {
            return commentService.findById(comment.getParentId())
                .orElseThrow(() -> new NotFoundException("신고된 답글의 댓글 정보가 없습니다."));
        }

        return comment;
    }

    private Post getReportedPost(Long contentId) {
        return postService.findById(contentId)
            .orElseThrow(() -> new NotFoundException("신고된 게시글 정보가 없습니다."));
    }

    private User getReportUser(Long userId) {
        return userService.findUser(userId)
            .orElseThrow(() -> new NotFoundException("신고자 정보가 없습니다."));
    }

    private Report getReport(Long reportId) {
        return reportRepository.findById(reportId)
            .orElseThrow(() -> new NotFoundException("신고된 정보가 없습니다."));
    }

    private Report getReport(Long reportId, ReportStatus currentReportStatus) {
        final Report report = getReport(reportId);

        if (report.getReportStatus() != currentReportStatus) {
            throw new BadRequestException("신고된 정보가 이미 변경되었습니다.");
        }

        return report;
    }

    public ReportDetailResponse updateReportStatus(
        String reportType, Long reportId, UpdateReportStatusRequest updateReportStatusRequest
    ) {
        final ReportType type = ReportType.fromValue(reportType);
        final ReportStatus currentReportStatus = getCurrentReportStatus(updateReportStatusRequest);
        final ReportStatus changeReportStatus = getChangeReportStatus(updateReportStatusRequest);
        final Report report = getReport(reportId, currentReportStatus);

        updateReportStatus(report, changeReportStatus, updateReportStatusRequest.getResult(), type);
        saveReport(report, changeReportStatus, updateReportStatusRequest.getResult());

        return getAdminReportDetail(reportType, reportId);
    }

    private void updateReportStatus(
        Report report, ReportStatus changeReportStatus, String result, ReportType type
    ) {
        if (StringUtils.isBlank(result)) {
            throw new BadRequestException("판단이유를 입력하세요.");
        }

        if (type == ReportType.POST) {
            postService.updateReportStatus(report.getContentId(), changeReportStatus);
        } else {
            commentService.updateReportStatus(report.getContentId(), changeReportStatus);
        }
    }

    private ReportStatus getCurrentReportStatus(UpdateReportStatusRequest updateReportStatusRequest) {
        try {
            return ReportStatus.fromValue(updateReportStatusRequest.getCurrentReportStatus());
        } catch (Exception e) {
            throw new BadRequestException("현재 신고 상태를 확인해주세요.");
        }
    }

    private ReportStatus getChangeReportStatus(UpdateReportStatusRequest updateReportStatusRequest) {
        try {
            return ReportStatus.fromValue(updateReportStatusRequest.getChangeReportStatus());
        } catch (Exception e) {
            throw new BadRequestException("변경하려는 신고 상태를 확인해주세요.");
        }
    }

    private ReportStatus getReportStatus(String reportStatusName) {
        try {
            return ReportStatus.fromValue(reportStatusName);
        } catch (Exception e) {
            throw new BadRequestException("신고 상태를 확인해주세요.");
        }
    }

    private void saveReport(Report report, ReportStatus reportStatus, String result) {
        User user = ActUserProvider.getNoneNull();

        ReportHistory insertHistory = new ReportHistory();
        insertHistory.setReportId(report.getId());
        insertHistory.setResult(result);
        insertHistory.setUserId(user.getId());
        insertHistory.setReportStatus(reportStatus);

        List<ReportHistory> reportHistoryList = report.getReportHistoryList();
        reportHistoryList.add(insertHistory);

        report.setReportStatus(reportStatus);
        report.setReportHistoryList(reportHistoryList);

        reportRepository.saveAndFlush(report);
    }

    private Page<ReportItemDto> getReportPostList(ReportType reportType, ReportStatus status, PageRequest pageRequest) {
        final long totalElements = adminReportRepository.countByReport(
            reportType, status
        );
        if (totalElements == 0L) {
            return new PageImpl<>(emptyList(), pageRequest, totalElements);
        }

        final List<ReportItemDto> reportResponses = adminReportRepository.findByReportPosts(
            reportType, status, pageRequest.getPageSize(), pageRequest.getPageNumber()
        );
        return new PageImpl<>(reportResponses, pageRequest, totalElements);
    }

    private Page<ReportItemDto> getReportCommentList(ReportType reportType, ReportStatus status, PageRequest pageRequest) {
        final long totalElements = adminReportRepository.countByReport(
            reportType, status
        );
        if (totalElements == 0L) {
            return new PageImpl<>(emptyList(), pageRequest, totalElements);
        }

        final List<ReportItemDto> reportResponses = adminReportRepository.findByReportComments(
            reportType, status, pageRequest.getPageSize(), pageRequest.getPageNumber()
        );
        return new PageImpl<>(reportResponses, pageRequest, totalElements);
    }
}
