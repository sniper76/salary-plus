package ag.act.api;

import ag.act.model.DeleteSolidarityApplicantRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.SolidarityLeaderApplicationResponse;
import ag.act.model.WithdrawSolidarityLeaderApplicantRequest;
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
 * A delegate to be called by the {@link AdminSolidarityLeaderApplicantApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminSolidarityLeaderApplicantApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * DELETE /api/admin/solidarity-leader-applicants/{solidarityId} : CMS 종목 유저의 주주대표 지원자 삭제하기
     *
     * @param solidarityId Solidarity Id (required)
     * @param deleteSolidarityApplicantRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminSolidarityLeaderApplicantApi#deleteSolidarityLeaderApplicant
     */
    default ResponseEntity<SimpleStringResponse> deleteSolidarityLeaderApplicant(Long solidarityId,
        DeleteSolidarityApplicantRequest deleteSolidarityApplicantRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/solidarity-leader-applicants/{solidarityLeaderApplicantId} : CMS용 주주대표 선출 공약 조회
     *
     * @param stockCode Stock code parameter (required)
     * @param solidarityLeaderElectionId Solidarity Leader Election ID parameter (required)
     * @param solidarityLeaderApplicantId Solidarity Leader Applicant ID parameter (required)
     * @return Successful response (status code 200)
     *         or Not Found (status code 404)
     * @see AdminSolidarityLeaderApplicantApi#getSolidarityLeaderApplicant
     */
    default ResponseEntity<SolidarityLeaderApplicationResponse> getSolidarityLeaderApplicant(String stockCode,
        Long solidarityLeaderElectionId,
        Long solidarityLeaderApplicantId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"reasonsForApply\" : \"reasonsForApply\", \"solidarityLeaderApplicantId\" : 5, \"solidarityLeaderElectionId\" : 1, \"commentsForStockHolder\" : \"commentsForStockHolder\", \"stock\" : { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" }, \"user\" : { \"phoneNumber\" : \"phoneNumber\", \"name\" : \"name\", \"nickname\" : \"nickname\", \"commentsForStockHolder\" : \"commentsForStockHolder\", \"id\" : 0, \"individualStockCountLabel\" : \"individualStockCountLabel\", \"solidarityApplicantId\" : 6, \"profileImageUrl\" : \"profileImageUrl\" }, \"applyStatus\" : \"applyStatus\", \"knowledgeOfCompanyManagement\" : \"knowledgeOfCompanyManagement\", \"goals\" : \"goals\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/admin/stocks/{stockCode}/solidarity-leader-applicants/{solidarityLeaderApplicantId}/withdraw : 관리자가 지원자의 지원을 철회한다.
     *
     * @param stockCode 종목 코드 (required)
     * @param solidarityLeaderApplicantId 지원서 ID (required)
     * @param withdrawSolidarityLeaderApplicantRequest  (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see AdminSolidarityLeaderApplicantApi#withdrawSolidarityLeaderApplicant
     */
    default ResponseEntity<SimpleStringResponse> withdrawSolidarityLeaderApplicant(String stockCode,
        Long solidarityLeaderApplicantId,
        WithdrawSolidarityLeaderApplicantRequest withdrawSolidarityLeaderApplicantRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
