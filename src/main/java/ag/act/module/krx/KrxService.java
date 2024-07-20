package ag.act.module.krx;

import ag.act.dto.krx.StkItemPriceDto;
import ag.act.dto.krx.StockBaseInfoDto;
import ag.act.dto.krx.StockItemDto;
import ag.act.dto.krx.StockPriceInfoDto;
import ag.act.enums.KrxServiceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

import static ag.act.enums.KrxServiceType.KNX_BYDD_TRD;
import static ag.act.enums.KrxServiceType.KNX_ISU_BASE_INFO;
import static ag.act.enums.KrxServiceType.KSQ_BYDD_TRD;
import static ag.act.enums.KrxServiceType.KSQ_ISU_BASE_INFO;
import static ag.act.enums.KrxServiceType.STK_BYDD_TRD;
import static ag.act.enums.KrxServiceType.STK_ISU_BASE_INFO;

@Slf4j
@Service
public class KrxService {
    private final KrxHttpClientUtil krxHttpClientUtil;

    public KrxService(KrxHttpClientUtil krxHttpClientUtil) {
        this.krxHttpClientUtil = krxHttpClientUtil;
    }

    public StockBaseInfoDto getBaseInfo(KrxServiceType krxServiceType, String queryValue) {
        return krxHttpClientUtil.callApi(
            krxServiceType,
            queryValue,
            StockBaseInfoDto.class
        );
    }

    public StockPriceInfoDto getDailyInfo(KrxServiceType krxServiceType, String queryValue) {
        return krxHttpClientUtil.callApi(
            krxServiceType,
            queryValue,
            StockPriceInfoDto.class
        );
    }

    public List<StkItemPriceDto> getAllIsuDailyInfos(String date) {
        return IntStream.range(0, 3)
            .parallel()
            .mapToObj((index) -> getDailyInfoByIndex(index, date))
            .map(StockPriceInfoDto::getOutBlock_1)
            .flatMap(List::stream)
            .toList();
    }

    public List<StockItemDto> getAllIsuBasicInfos(String date) {
        return IntStream.range(0, 3)
            .parallel()
            .mapToObj((index) -> getBaseInfoByIndex(index, date))
            .map(StockBaseInfoDto::getOutBlock_1)
            .flatMap(List::stream)
            .toList();
    }

    private StockBaseInfoDto getBaseInfoByIndex(int index, String date) {
        return switch (index) {
            case 0 -> getBaseInfo(STK_ISU_BASE_INFO, date);
            case 1 -> getBaseInfo(KSQ_ISU_BASE_INFO, date);
            default -> getBaseInfo(KNX_ISU_BASE_INFO, date);
        };
    }

    private StockPriceInfoDto getDailyInfoByIndex(int index, String date) {
        return switch (index) {
            case 0 -> getDailyInfo(STK_BYDD_TRD, date);
            case 1 -> getDailyInfo(KSQ_BYDD_TRD, date);
            default -> getDailyInfo(KNX_BYDD_TRD, date);
        };
    }
}
