/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.StockHomeResponse;
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
public interface StockHomeApi {

    default StockHomeApiDelegate getDelegate() {
        return new StockHomeApiDelegate() {};
    }

    /**
     * GET /api/stocks/{stockCode}/home : 종목 홈 화면 조회
     *
     * @param stockCode Stock code parameter (required)
     * @return Success (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/stocks/{stockCode}/home",
        produces = { "application/json" }
    )
    default ResponseEntity<StockHomeResponse> getStockHome(
         @PathVariable("stockCode") String stockCode
    ) {
        return getDelegate().getStockHome(stockCode);
    }

}
