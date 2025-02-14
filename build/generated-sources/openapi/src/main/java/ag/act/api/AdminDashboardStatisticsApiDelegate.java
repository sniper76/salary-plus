package ag.act.api;

import ag.act.model.DashboardStatisticsAgeDataResponse;
import ag.act.model.DashboardStatisticsDataResponse;
import ag.act.model.DashboardStatisticsGenderDataResponse;
import ag.act.model.ErrorResponse;
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
 * A delegate to be called by the {@link AdminDashboardStatisticsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminDashboardStatisticsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/admin/dashboard/statistics : CMS 대쉬보드 전체/종목별 일별/월별 통계 조회
     * 대쉬보드 화면 상단 전체 정보와 하단의 종목별 정보 파라미터별로 조회한다.
     *
     * @param type DashboardStatisticsType 기본값 없으면 전체 차트 조회 (optional)
     * @param stockCode stockCode 기본값 없으면 전체 차트 조회 (optional)
     * @param periodType DAILY or MONTHLY 기본값은 DAILY (optional)
     * @param searchFrom 2023-11-24 or 2023-05 is period 7 (optional)
     * @param searchTo 2023-11-30 or 2023-11 is period 7 (optional)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminDashboardStatisticsApi#getStatistics
     */
    default ResponseEntity<DashboardStatisticsDataResponse> getStatistics(String type,
        String stockCode,
        String periodType,
        String searchFrom,
        String searchTo) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"search\" : { \"period\" : 0, \"from\" : \"from\", \"to\" : \"to\" }, \"data\" : [ { \"summary\" : { \"upDownText\" : \"upDownText\", \"upDownPercent\" : \"upDownPercent\", \"upDown\" : \"upDown\" }, \"periodType\" : \"periodType\", \"type\" : \"type\", \"title\" : \"title\", \"value\" : 6.027456183070403, \"items\" : [ { \"value\" : 1.4658129805029452, \"key\" : \"key\" }, { \"value\" : 1.4658129805029452, \"key\" : \"key\" } ] }, { \"summary\" : { \"upDownText\" : \"upDownText\", \"upDownPercent\" : \"upDownPercent\", \"upDown\" : \"upDown\" }, \"periodType\" : \"periodType\", \"type\" : \"type\", \"title\" : \"title\", \"value\" : 6.027456183070403, \"items\" : [ { \"value\" : 1.4658129805029452, \"key\" : \"key\" }, { \"value\" : 1.4658129805029452, \"key\" : \"key\" } ] } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/dashboard/statistics/age : CMS 대쉬보드 나이별 일별/월별 통계 조회
     *
     * @param periodType DAILY or MONTHLY 기본값은 DAILY (optional)
     * @param searchFrom 2023-11-24 or 2023-05 is period 7 (optional)
     * @param searchTo 2023-11-30 or 2023-11 is period 7 (optional)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminDashboardStatisticsApi#getStatisticsAge
     */
    default ResponseEntity<DashboardStatisticsAgeDataResponse> getStatisticsAge(String periodType,
        String searchFrom,
        String searchTo) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"search\" : { \"period\" : 0, \"from\" : \"from\", \"to\" : \"to\" }, \"data\" : { \"total\" : 0, \"periodType\" : \"periodType\", \"age10\" : { \"upDownText\" : \"upDownText\", \"upDownPercent\" : \"upDownPercent\", \"title\" : \"title\", \"upDown\" : \"upDown\", \"value\" : 6, \"percent\" : \"percent\" }, \"age20\" : { \"upDownText\" : \"upDownText\", \"upDownPercent\" : \"upDownPercent\", \"title\" : \"title\", \"upDown\" : \"upDown\", \"value\" : 6, \"percent\" : \"percent\" }, \"age30\" : { \"upDownText\" : \"upDownText\", \"upDownPercent\" : \"upDownPercent\", \"title\" : \"title\", \"upDown\" : \"upDown\", \"value\" : 6, \"percent\" : \"percent\" }, \"type\" : \"type\", \"title\" : \"title\", \"age40\" : { \"upDownText\" : \"upDownText\", \"upDownPercent\" : \"upDownPercent\", \"title\" : \"title\", \"upDown\" : \"upDown\", \"value\" : 6, \"percent\" : \"percent\" }, \"age50\" : { \"upDownText\" : \"upDownText\", \"upDownPercent\" : \"upDownPercent\", \"title\" : \"title\", \"upDown\" : \"upDown\", \"value\" : 6, \"percent\" : \"percent\" }, \"age60\" : { \"upDownText\" : \"upDownText\", \"upDownPercent\" : \"upDownPercent\", \"title\" : \"title\", \"upDown\" : \"upDown\", \"value\" : 6, \"percent\" : \"percent\" }, \"age70\" : { \"upDownText\" : \"upDownText\", \"upDownPercent\" : \"upDownPercent\", \"title\" : \"title\", \"upDown\" : \"upDown\", \"value\" : 6, \"percent\" : \"percent\" } } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/dashboard/statistics/gender : CMS 대쉬보드 성별 일별/월별 통계 조회
     *
     * @param periodType DAILY or MONTHLY 기본값은 DAILY (optional)
     * @param searchFrom 2023-11-24 or 2023-05 is period 7 (optional)
     * @param searchTo 2023-11-30 or 2023-11 is period 7 (optional)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminDashboardStatisticsApi#getStatisticsGender
     */
    default ResponseEntity<DashboardStatisticsGenderDataResponse> getStatisticsGender(String periodType,
        String searchFrom,
        String searchTo) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"search\" : { \"period\" : 0, \"from\" : \"from\", \"to\" : \"to\" }, \"data\" : { \"total\" : 0, \"periodType\" : \"periodType\", \"type\" : \"type\", \"title\" : \"title\", \"female\" : { \"upDownText\" : \"upDownText\", \"upDownPercent\" : \"upDownPercent\", \"title\" : \"title\", \"upDown\" : \"upDown\", \"value\" : 6, \"percent\" : \"percent\" }, \"male\" : { \"upDownText\" : \"upDownText\", \"upDownPercent\" : \"upDownPercent\", \"title\" : \"title\", \"upDown\" : \"upDown\", \"value\" : 6, \"percent\" : \"percent\" } } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
