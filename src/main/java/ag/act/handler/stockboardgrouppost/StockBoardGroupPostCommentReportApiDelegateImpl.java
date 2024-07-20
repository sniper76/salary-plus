package ag.act.handler.stockboardgrouppost;

import ag.act.api.StockBoardGroupPostCommentReportApiDelegate;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.enums.ReportType;
import ag.act.model.ReportContentRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@UseGuards({IsActiveUserGuard.class})
public class StockBoardGroupPostCommentReportApiDelegateImpl implements StockBoardGroupPostCommentReportApiDelegate {
    private final ReportService reportService;

    public StockBoardGroupPostCommentReportApiDelegateImpl(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public ResponseEntity<SimpleStringResponse> reportBoardGroupPostComment(
        String stockCode, String boardGroupName,
        Long postId, Long commentId, ReportContentRequest reportContentRequest
    ) {
        return ResponseEntity.ok(
            reportService.reportBoardGroupPostAndComment(
                commentId, ReportType.COMMENT, reportContentRequest
            )
        );
    }
}
