package ag.act.handler.stockboardgrouppost;

import ag.act.api.StockBoardGroupPostReportApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.enums.ReportType;
import ag.act.model.ReportContentRequest;
import ag.act.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@UseGuards({IsActiveUserGuard.class})
public class StockBoardGroupPostReportApiDelegateImpl implements StockBoardGroupPostReportApiDelegate {
    private final ReportService reportService;

    public StockBoardGroupPostReportApiDelegateImpl(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public ResponseEntity<ag.act.model.SimpleStringResponse> reportBoardGroupPost(
        String stockCode, String boardGroupName,
        Long postId, ReportContentRequest reportContentRequest
    ) {
        return ResponseEntity.ok(
            reportService.reportBoardGroupPostAndComment(
                postId, ReportType.POST, reportContentRequest
            )
        );
    }
}
