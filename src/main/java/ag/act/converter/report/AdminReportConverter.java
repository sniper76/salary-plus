package ag.act.converter.report;

import ag.act.converter.DateTimeConverter;
import ag.act.converter.post.PostResponseConverter;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Report;
import ag.act.entity.ReportHistory;
import ag.act.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdminReportConverter {
    private final PostResponseConverter postResponseConverter;
    private final AdminReportCommentResponseConverter adminReportCommentResponseConverter;

    private ag.act.model.ReportResponse convertReportResponse(Report report, String nickname) {
        return new ag.act.model.ReportResponse()
            .reportId(report.getId())
            .contentId(report.getContentId())
            .contentType(report.getType().name())
            .reportStatus(report.getReportStatus().name())
            .nickname(nickname)
            .reason(report.getReason())
            .reportHistoryList(convertReportHistoryList(report.getReportHistoryList()))
            .createdAt(DateTimeConverter.convert(report.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(report.getUpdatedAt()));
    }

    private List<ag.act.model.ReportHistoryResponse> convertReportHistoryList(List<ReportHistory> historyList) {
        return historyList.stream()
            .map(element -> new ag.act.model.ReportHistoryResponse()
                .userId(element.getUserId())
                .reportStatus(element.getReportStatus().name())
                .result(element.getResult())
                .createdAt(DateTimeConverter.convert(element.getCreatedAt()))
                .updatedAt(DateTimeConverter.convert(element.getUpdatedAt())))
            .toList();
    }

    public ag.act.model.ReportDetailResponse convertResponse(
        Post post, User reportUser, Report report
    ) {
        return new ag.act.model.ReportDetailResponse()
            .post(postResponseConverter.convert(post, true))
            .report(convertReportResponse(report, reportUser.getNickname()));
    }

    public ag.act.model.ReportDetailResponse convertResponse(
        Comment comment, User reportUser, List<Comment> replyList, Report report
    ) {
        return new ag.act.model.ReportDetailResponse()
            .comment(adminReportCommentResponseConverter.convert(comment))
            .reply(replyList.stream().map(adminReportCommentResponseConverter::convert).collect(Collectors.toList()))
            .report(convertReportResponse(report, reportUser.getNickname()));
    }
}
