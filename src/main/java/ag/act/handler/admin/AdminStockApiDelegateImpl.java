package ag.act.handler.admin;

import ag.act.api.AdminStockApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.stock.GetStockStatisticsSearchDto;
import ag.act.dto.stock.GetStocksSearchDto;
import ag.act.facade.stock.StockFacade;
import ag.act.model.GetStockDetailsDataResponse;
import ag.act.model.GetStockStatisticsDataResponse;
import ag.act.model.GetStocksResponse;
import ag.act.model.StockDataArrayResponse;
import ag.act.util.DownloadFileUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@UseGuards(IsAdminGuard.class)
public class AdminStockApiDelegateImpl implements AdminStockApiDelegate {

    private final StockFacade stockFacade;
    private final PageDataConverter pageDataConverter;

    @Override
    public ResponseEntity<StockDataArrayResponse> getStocksAutoComplete(String searchKeyword) {
        return ResponseEntity.ok(stockFacade.getStocksAutoComplete(searchKeyword));
    }

    @Override
    public ResponseEntity<Resource> downloadUsersByStockCodeCsv(String stockCode) {
        return DownloadFileUtil.ok(stockFacade.downloadUsersByStockCodeInCsv(stockCode));
    }

    @Override
    public ResponseEntity<GetStocksResponse> getStocks(
        String code,
        Integer page,
        Integer size,
        List<String> sorts,
        Boolean isOnlyPrivateStocks
    ) {

        final GetStocksSearchDto getStocksSearchDto = GetStocksSearchDto.builder()
            .code(code)
            .isOnlyPrivateStocks(isOnlyPrivateStocks)
            .pageRequest(pageDataConverter.convert(getPageBasedOnStockCode(code, page), size, sorts))
            .build();

        return ResponseEntity.ok(
            pageDataConverter.convert(
                stockFacade.getStocks(getStocksSearchDto),
                GetStocksResponse.class
            )
        );
    }

    @Override
    public ResponseEntity<GetStockDetailsDataResponse> getStockDetails(String code) {
        return ResponseEntity.ok(
            new GetStockDetailsDataResponse()
                .data(stockFacade.getStockDetails(code))
        );
    }

    @Override
    public ResponseEntity<GetStockStatisticsDataResponse> getStockStatistics(String code, String type, String periodType, String period) {

        final GetStockStatisticsSearchDto getStockStatisticsSearchDto = new GetStockStatisticsSearchDto(code, type, periodType, period);

        return ResponseEntity.ok(
            new GetStockStatisticsDataResponse()
                .data(stockFacade.getStockStatistics(getStockStatisticsSearchDto))
        );
    }

    private Integer getPageBasedOnStockCode(String code, Integer page) {
        if (StringUtils.isNotBlank(code)) {
            return 1; // 검색어가 있을 경우 1페이지로 고정
        }
        return page;
    }
}
