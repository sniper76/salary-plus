package ag.act.service.stock;

import ag.act.converter.stock.SolidarityResponseConverter;
import ag.act.entity.Solidarity;
import ag.act.model.SimpleStringResponse;
import ag.act.model.SolidarityDataResponse;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StockSolidarityService {

    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    private final SolidarityResponseConverter solidarityResponseConverter;
    private final SolidarityService solidarityService;

    public SolidarityDataResponse getSolidarity(String stockCode) {
        final Solidarity solidarity = solidarityService.getSolidarityByStockCode(stockCode);

        return new SolidarityDataResponse()
            .data(solidarityResponseConverter.convert(solidarity));
    }

    public SimpleStringResponse applySolidarityLeader(String stockCode) {
        solidarityLeaderApplicantService.applyForLeader(stockCode);

        return SimpleStringResponseUtil.ok();
    }

    public SimpleStringResponse cancelSolidarityLeaderApplication(String stockCode) {
        solidarityLeaderApplicantService.cancelLeaderApplication(stockCode);

        return SimpleStringResponseUtil.ok();
    }
}
