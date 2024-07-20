package ag.act.service;

import ag.act.entity.MyDataSummary;
import ag.act.entity.mydata.JsonMyData;
import ag.act.module.mydata.MyDataSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JsonMyDataStockService {
    private final MyDataSummaryService myDataSummaryService;

    public Optional<JsonMyData> getJsonMyData(Long userId) {
        return myDataSummaryService.findByUserId(userId)
            .map(MyDataSummary::getJsonMyData);
    }
}
