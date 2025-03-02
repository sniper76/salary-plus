/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.CreateStockSearchTrendRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.SimpleStringResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
@Validated
public interface StockSearchTrendsApi {

    default StockSearchTrendsApiDelegate getDelegate() {
        return new StockSearchTrendsApiDelegate() {};
    }

    /**
     * POST /api/stock-search-trends
     *
     * @param createStockSearchTrendRequest  (required)
     * @return Success (status code 200)
     *         or Bad Request (status code 400)
     *         or Internal Server Error (status code 500)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/stock-search-trends",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> createStockSearchTrend(
         @Valid @RequestBody CreateStockSearchTrendRequest createStockSearchTrendRequest
    ) {
        return getDelegate().createStockSearchTrend(createStockSearchTrendRequest);
    }

}
