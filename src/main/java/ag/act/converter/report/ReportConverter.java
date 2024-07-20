package ag.act.converter.report;

import ag.act.converter.DateTimeConverter;
import ag.act.dto.ReportItemDto;
import ag.act.entity.Report;
import ag.act.enums.ReportType;
import org.springframework.stereotype.Component;

@Component
public class ReportConverter {

    public Report convert(Long userId, Long contentId, String reason, ReportType type, ag.act.model.ReportStatus status) {
        Report report = new Report();
        report.setReportStatus(status);
        report.setUserId(userId);
        report.setContentId(contentId);
        report.setReason(reason);
        report.setType(type);
        return report;
    }

    public ag.act.model.ReportListResponse convertResponse(ReportItemDto reportItem) {
        return new ag.act.model.ReportListResponse()
            .reportId(reportItem.getId())
            .contentId(reportItem.getContentId())
            .title(reportItem.getTitle())
            .contentType(reportItem.getReportType().name())
            .boardCategoryName(reportItem.getBoardCategoryName())
            .boardCategoryDisplayName(null)
            .boardGroupName(reportItem.getBoardGroupName())
            .reportStatus(reportItem.getReportStatus().name())
            .stockCode(reportItem.getStockCode())
            .stockName(reportItem.getStockName())
            .likeCount(reportItem.getLikeCount())
            .commentCount(reportItem.getCommentCount())
            .replyCount(reportItem.getReplyCount())
            .viewCount(reportItem.getViewCount())
            .createdAt(DateTimeConverter.convert(reportItem.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(reportItem.getUpdatedAt()));
    }
}
