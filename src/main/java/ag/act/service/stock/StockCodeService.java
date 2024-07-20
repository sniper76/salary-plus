package ag.act.service.stock;

import ag.act.dto.MySolidarityDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockCodeService {
    public List<String> getStockCodes(List<MySolidarityDto> mySolidarities) {
        return mySolidarities.stream().map(dto -> dto.getStock().getCode()).toList();
    }
}
