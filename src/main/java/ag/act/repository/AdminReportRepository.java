package ag.act.repository;

import ag.act.dto.ReportItemDto;
import ag.act.enums.ReportType;

import java.util.List;

public interface AdminReportRepository {
    long countByReport(ReportType reportType, ag.act.model.ReportStatus reportStatus);

    List<ReportItemDto> findByReportPosts(ReportType reportType, ag.act.model.ReportStatus reportStatus, int size, int page);

    List<ReportItemDto> findByReportComments(ReportType reportType, ag.act.model.ReportStatus reportStatus, int size, int page);
}
