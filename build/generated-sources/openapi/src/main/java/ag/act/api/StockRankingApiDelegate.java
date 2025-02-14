package ag.act.api;

import ag.act.model.StockRankingDataResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * A delegate to be called by the {@link StockRankingApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface StockRankingApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/stock-rankings : 종목 랭킹 조회
     *
     * @param size size of the ranking list (optional, default to 50)
     * @param sorts Sorting criteria (optional, default to stakeRank:asc)
     * @return Successful response (status code 200)
     * @see StockRankingApi#getStockRankings
     */
    default ResponseEntity<StockRankingDataResponse> getStockRankings(Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"stake\" : \"stake\", \"stockName\" : \"stockName\", \"marketValue\" : \"marketValue\", \"stakeRank\" : 0, \"marketValueRankDelta\" : 5, \"marketValueRank\" : 1, \"stakeRankDelta\" : 6, \"stockCode\" : \"stockCode\" }, { \"stake\" : \"stake\", \"stockName\" : \"stockName\", \"marketValue\" : \"marketValue\", \"stakeRank\" : 0, \"marketValueRankDelta\" : 5, \"marketValueRank\" : 1, \"stakeRankDelta\" : 6, \"stockCode\" : \"stockCode\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
