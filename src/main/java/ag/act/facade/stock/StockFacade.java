package ag.act.facade.stock;

import ag.act.converter.stock.SimpleStockResponseConverter;
import ag.act.converter.stock.SolidarityStatisticsDailyResponseConverter;
import ag.act.converter.stock.StockDataArrayResponseConverter;
import ag.act.converter.stock.StockSearchResultItemResponseConverter;
import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.SimplePageDto;
import ag.act.dto.download.DownloadFile;
import ag.act.dto.stock.GetStockStatisticsSearchDto;
import ag.act.dto.stock.GetStocksSearchDto;
import ag.act.entity.Stock;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.InternalServerException;
import ag.act.model.SimpleStockResponse;
import ag.act.model.StockStatisticsResponse;
import ag.act.service.admin.stock.StockDetailsService;
import ag.act.service.solidarity.SolidarityStatisticsService;
import ag.act.service.stock.StockService;
import ag.act.service.user.download.csv.UserDownloadService;
import ag.act.util.DownloadFileUtil;
import ag.act.util.FilenameUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class StockFacade {
    private final StockService stockService;
    private final UserDownloadService userDownloadService;
    private final StockDataArrayResponseConverter stockConverter;
    private final StockSearchResultItemResponseConverter stockSearchResultItemResponseConverter;
    private final SimpleStockResponseConverter simpleStockResponseConverter;
    private final SolidarityStatisticsService solidarityStatisticsService;
    private final SolidarityStatisticsDailyResponseConverter solidarityStatisticsDailyResponseConverter;
    private final StockDetailsService stockDetailsService;

    @SuppressWarnings("ConstantConditions")
    public DownloadFile downloadUsersByStockCodeInCsv(String stockCode) {
        final HttpServletResponse response = RequestContextHolder.getResponse();
        final String csvFilename = getCsvFilenameForStockUserList(stockCode);

        try {
            DownloadFileUtil.setFilename(response, csvFilename);
            userDownloadService.downloadUsersByStockCodeCsv(stockCode, response);
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("CSV 파일 다운로드 중 알 수 없는 오류가 발생했습니다.", e);
        }

        return DownloadFile.builder().fileName(csvFilename).build();
    }

    private String getCsvFilenameForStockUserList(String stockCode) {
        final String stockName = stockService.getStock(stockCode).getName();
        return FilenameUtil.getFilenameWithDate("%s_주주명부".formatted(stockName), "csv");
    }

    public ag.act.model.StockDataArrayResponse getStocksAutoComplete(String searchKeyword) {
        return stockConverter.convert(
            stockService.getMostRelatedTopTenStocksBySearchKeyword(searchKeyword)
        );
    }

    public Stock findByCode(String stockCode) {
        return stockService.getStock(stockCode);
    }

    public SimplePageDto<ag.act.model.StockResponse> getStocks(GetStocksSearchDto getStocksSearchDto) {
        return new SimplePageDto<>(
            stockService.getStocks(getStocksSearchDto)
                .map(stockSearchResultItemResponseConverter::convert)
        );
    }

    public List<SimpleStockResponse> getSimpleStocks() {
        return stockService.getAllSimpleStocks()
            .stream()
            .map(simpleStockResponseConverter::convert)
            .toList();
    }

    public List<SimpleStockResponse> getSimpleStocksWithoutTestStocks() {
        return stockService.findAllSimpleStocksWithoutTestStocks()
            .stream()
            .map(simpleStockResponseConverter::convert)
            .toList();
    }

    public List<StockStatisticsResponse> getStockStatistics(GetStockStatisticsSearchDto getStockStatisticsSearchDto) {
        return solidarityStatisticsDailyResponseConverter.convert(
            solidarityStatisticsService.getSolidarityStatistics(getStockStatisticsSearchDto),
            getStockStatisticsSearchDto.getType()
        );
    }

    public ag.act.model.StockDetailsResponse getStockDetails(String stockCode) {
        return stockDetailsService.getStockDetails(stockCode);
    }
}
