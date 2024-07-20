package ag.act.handler;

import ag.act.api.SolidarityLeaderElectionApiDelegate;
import ag.act.converter.stock.SolidarityLeaderElectionApplyRequestConverter;
import ag.act.core.guard.HoldingStockGuard;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.facade.solidarity.leader.SolidarityLeaderElectionFacade;
import ag.act.model.GetSolidarityLeaderApplicantResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.SolidarityLeaderApplicationResponse;
import ag.act.model.SolidarityLeaderElectionApplyRequest;
import ag.act.model.SolidarityLeaderElectionProceduresDataResponse;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SolidarityLeaderElectionApiDelegateImpl implements SolidarityLeaderElectionApiDelegate {

    private final SolidarityLeaderElectionFacade solidarityLeaderElectionFacade;
    private final SolidarityLeaderElectionApplyRequestConverter solidarityLeaderElectionApplyRequestConverter;

    @UseGuards(IsActiveUserGuard.class)
    @Override
    public ResponseEntity<SolidarityLeaderElectionProceduresDataResponse> getSolidarityLeaderElectionProcedures() {
        return ResponseEntity.ok(solidarityLeaderElectionFacade.getProcedures());
    }

    @UseGuards(HoldingStockGuard.class)
    @Override
    public ResponseEntity<SimpleStringResponse> createSolidarityLeaderApplicant(
        String stockCode,
        SolidarityLeaderElectionApplyRequest solidarityLeaderElectionApplyRequest
    ) {
        solidarityLeaderElectionFacade.createSolidarityLeaderApplicant(
            solidarityLeaderElectionApplyRequestConverter.convert(solidarityLeaderElectionApplyRequest, stockCode)
        );

        return SimpleStringResponseUtil.okResponse();
    }

    @UseGuards(HoldingStockGuard.class)
    @Override
    public ResponseEntity<SimpleStringResponse> updateSolidarityLeaderApplicant(
        String stockCode,
        Long solidarityLeaderElectionId,
        Long solidarityLeaderApplicantId,
        SolidarityLeaderElectionApplyRequest solidarityLeaderElectionApplyRequest
    ) {
        solidarityLeaderElectionFacade.updateSolidarityLeaderApplicant(
            solidarityLeaderElectionApplyRequestConverter.convert(
                solidarityLeaderElectionApplyRequest,
                stockCode,
                solidarityLeaderElectionId,
                solidarityLeaderApplicantId
            )
        );

        return SimpleStringResponseUtil.okResponse();
    }

    @UseGuards(HoldingStockGuard.class)
    @Override
    public ResponseEntity<GetSolidarityLeaderApplicantResponse> getSolidarityLeaderApplicants(
        String stockCode,
        Long solidarityLeaderElectionId
    ) {
        return ResponseEntity.ok(
            solidarityLeaderElectionFacade.getSolidarityLeaderApplicants(
                stockCode,
                solidarityLeaderElectionId
            )
        );
    }

    @UseGuards(HoldingStockGuard.class)
    @Override
    public ResponseEntity<SolidarityLeaderApplicationResponse> getSolidarityLeaderApplication(
        String stockCode,
        Long solidarityLeaderElectionId,
        Long solidarityLeaderApplicantId
    ) {
        return ResponseEntity.ok(
            solidarityLeaderElectionFacade.getSolidarityLeaderApplication(
                stockCode,
                solidarityLeaderElectionId,
                solidarityLeaderApplicantId
            )
        );
    }

    @UseGuards(HoldingStockGuard.class)
    @Override
    public ResponseEntity<SimpleStringResponse> withdrawSolidarityLeaderApplication(
        String stockCode,
        Long solidarityLeaderElectionId,
        Long solidarityLeaderApplicantId
    ) {
        solidarityLeaderElectionFacade.withdrawSolidarityLeaderApplicant(solidarityLeaderElectionId, solidarityLeaderApplicantId);
        return SimpleStringResponseUtil.okResponse();
    }

    @UseGuards(HoldingStockGuard.class)
    @Override
    public ResponseEntity<SolidarityLeaderApplicationResponse> getLatestSolidarityLeaderApplication(String stockCode) {
        return ResponseEntity.ok(
            solidarityLeaderElectionFacade.getLatestApplication(stockCode)
        );
    }
}
