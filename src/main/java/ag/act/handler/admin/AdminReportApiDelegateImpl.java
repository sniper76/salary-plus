package ag.act.handler.admin;

import ag.act.api.AdminReportApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.GetReportResponse;
import ag.act.model.ReportDetailResponse;
import ag.act.model.UpdateReportStatusRequest;
import ag.act.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@UseGuards(IsAdminGuard.class)
public class AdminReportApiDelegateImpl implements AdminReportApiDelegate {
    private final ReportService reportService;
    private final PageDataConverter pageDataConverter;

    @Override
    public ResponseEntity<GetReportResponse> getReports(
        String reportType, Integer page, Integer size, String reportStatus, List<String> sorts
    ) {
        final PageRequest pageRequest = pageDataConverter.convert(page, size, sorts);
        return ResponseEntity.ok(reportService.getAdminReports(reportType, reportStatus, pageRequest));
    }

    @Override
    public ResponseEntity<ReportDetailResponse> getReportDetail(String reportType, Long reportId) {
        return ResponseEntity.ok(reportService.getAdminReportDetail(reportType, reportId));
    }

    public ResponseEntity<ReportDetailResponse> updateReportStatus(
        String reportType, Long reportId, UpdateReportStatusRequest updateReportStatusRequest
    ) {
        return ResponseEntity.ok(reportService.updateReportStatus(reportType, reportId, updateReportStatusRequest));
    }


}
