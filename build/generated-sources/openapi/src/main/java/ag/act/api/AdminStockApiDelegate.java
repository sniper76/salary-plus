package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.GetStockDetailsDataResponse;
import ag.act.model.GetStockStatisticsDataResponse;
import ag.act.model.GetStocksResponse;
import ag.act.model.StockDataArrayResponse;
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
 * A delegate to be called by the {@link AdminStockApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminStockApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/stocks/{stockCode}/users/csv-download : 해당 종목을 보유한 유저정보 엑셀 파일 다운로드
     *
     * @param stockCode Stock code parameter (required)
     * @return File download successfully (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see AdminStockApi#downloadUsersByStockCodeCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadUsersByStockCodeCsv(String stockCode) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/stocks/{code} : CMS 종목 상세 조회
     *
     * @param code Stock code (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminStockApi#getStockDetails
     */
    default ResponseEntity<GetStockDetailsDataResponse> getStockDetails(String code) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"solidarityLeaderApplicants\" : [ { \"phoneNumber\" : \"phoneNumber\", \"name\" : \"name\", \"nickname\" : \"nickname\", \"commentsForStockHolder\" : \"commentsForStockHolder\", \"id\" : 0, \"individualStockCountLabel\" : \"individualStockCountLabel\", \"solidarityApplicantId\" : 6, \"profileImageUrl\" : \"profileImageUrl\" }, { \"phoneNumber\" : \"phoneNumber\", \"name\" : \"name\", \"nickname\" : \"nickname\", \"commentsForStockHolder\" : \"commentsForStockHolder\", \"id\" : 0, \"individualStockCountLabel\" : \"individualStockCountLabel\", \"solidarityApplicantId\" : 6, \"profileImageUrl\" : \"profileImageUrl\" } ], \"solidarityLeader\" : { \"id\" : \"1\", \"name\" : \"이동훈\", \"email\" : \"yanggun7201@gmail.com\", \"birthDate\" : \"1999-08-07T12:00:00.000Z\", \"phoneNumber\" : \"01055556666\", \"gender\" : \"M\", \"nickname\" : null, \"status\" : \"ACTIVE\", \"message\" : \"안녕하세요. 잘 부탁합니다.\", \"solidarityId\" : 124, \"corporateNo\" : \"법인번호\" }, \"solidarity\" : { \"stake\" : 1.4658129, \"requiredMemberCount\" : 5, \"code\" : \"code\", \"representativePhoneNumber\" : \"(02) 555-6666\", \"minThresholdMemberCount\" : 33861, \"memberCount\" : 6, \"name\" : \"name\", \"id\" : 0 }, \"todayDelta\" : { \"items\" : [ { \"title\" : \"title\", \"value\" : \"value\", \"variation\" : { \"color\" : \"#FF0000\", \"text\" : \"text\" } }, { \"title\" : \"title\", \"value\" : \"value\", \"variation\" : { \"color\" : \"#FF0000\", \"text\" : \"text\" } } ], \"descriptionLabel\" : \"최종 업데이트(전일대비): 2023-08-23 21:49\" }, \"acceptUser\" : { \"phoneNumber\" : \"phoneNumber\", \"name\" : \"name\", \"corporateNo\" : \"corporateNo\", \"id\" : 0, \"birthDate\" : \"2000-01-23T04:56:07.000+00:00\" } } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/stocks/{code}/statistics/{type}/{periodType} : CMS 종목 통계 정보 일별/월별 조회
     *
     * @param code Stock code (required)
     * @param type STOCK_QUANTITY(주식수) OR MEMBER_COUNT(주주수) OR MARKET_VALUE(시가액) OR STAKE(지분율) (required)
     * @param periodType DAILY OR MONTHLY (required)
     * @param period 202310 for DAILY or 2023 for MONTHLY (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminStockApi#getStockStatistics
     */
    default ResponseEntity<GetStockStatisticsDataResponse> getStockStatistics(String code,
        String type,
        String periodType,
        String period) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"value\" : 1000, \"key\" : \"2023-10-01, 2023-10\" }, { \"value\" : 1000, \"key\" : \"2023-10-01, 2023-10\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/stocks : CMS 종목 목록 조회하기
     *
     * @param code Stock code (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to stake:desc)
     * @param isOnlyPrivateStocks filter only private stocks (optional, default to false)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminStockApi#getStocks
     */
    default ResponseEntity<GetStocksResponse> getStocks(String code,
        Integer page,
        Integer size,
        List<String> sorts,
        Boolean isOnlyPrivateStocks) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"code\" : \"종목코드\", \"name\" : \"종목명\", \"totalIssuedQuantity\" : \"상장주식수\", \"representativePhoneNumber\" : \"(02) 555-6666\", \"memberCount\" : \"주주수\", \"stake\" : \"지분율\" }, { \"code\" : \"종목코드\", \"name\" : \"종목명\", \"totalIssuedQuantity\" : \"상장주식수\", \"representativePhoneNumber\" : \"(02) 555-6666\", \"memberCount\" : \"주주수\", \"stake\" : \"지분율\" } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/stocks/auto-complete : 종목명 자동 완성
     *
     * @param searchKeyword Search keyword (optional)
     * @return Success (status code 200)
     * @deprecated
     * @see AdminStockApi#getStocksAutoComplete
     */
    @Deprecated
    default ResponseEntity<StockDataArrayResponse> getStocksAutoComplete(String searchKeyword) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"code\" : \"종목코드\", \"name\" : \"종목명\", \"totalIssuedQuantity\" : \"상장주식수\", \"representativePhoneNumber\" : \"(02) 555-6666\", \"memberCount\" : \"주주수\", \"stake\" : \"지분율\" }, { \"code\" : \"종목코드\", \"name\" : \"종목명\", \"totalIssuedQuantity\" : \"상장주식수\", \"representativePhoneNumber\" : \"(02) 555-6666\", \"memberCount\" : \"주주수\", \"stake\" : \"지분율\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
