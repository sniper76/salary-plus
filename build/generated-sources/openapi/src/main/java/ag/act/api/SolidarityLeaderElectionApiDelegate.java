package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.GetSolidarityLeaderApplicantResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.SolidarityLeaderApplicationResponse;
import ag.act.model.SolidarityLeaderElectionApplyRequest;
import ag.act.model.SolidarityLeaderElectionProceduresDataResponse;
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
 * A delegate to be called by the {@link SolidarityLeaderElectionApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface SolidarityLeaderElectionApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/stocks/{stockCode}/solidarity-leader-elections/solidarity-leader-applicants : 주주대표 지원하기
     *
     * @param stockCode Stock Code (required)
     * @param solidarityLeaderElectionApplyRequest  (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see SolidarityLeaderElectionApi#createSolidarityLeaderApplicant
     */
    default ResponseEntity<SimpleStringResponse> createSolidarityLeaderApplicant(String stockCode,
        SolidarityLeaderElectionApplyRequest solidarityLeaderElectionApplyRequest) {
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
     * GET /api/stocks/{stockCode}/solidarity-leader-elections/solidarity-leader-applicants/latest : 가장 최근 작성한 지원서 조회, 지원서 id, 선출 id, 제출 상태는 포함되지 않고 전달됩니다.
     *
     * @param stockCode Stock Code (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see SolidarityLeaderElectionApi#getLatestSolidarityLeaderApplication
     */
    default ResponseEntity<SolidarityLeaderApplicationResponse> getLatestSolidarityLeaderApplication(String stockCode) {
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
     * GET /api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/solidarity-leader-applicants : 주주대표 지원자 목록 조회
     *
     * @param stockCode Stock code parameter (required)
     * @param solidarityLeaderElectionId Solidarity Leader Election Id (required)
     * @return Successful response (status code 200)
     * @see SolidarityLeaderElectionApi#getSolidarityLeaderApplicants
     */
    default ResponseEntity<GetSolidarityLeaderApplicantResponse> getSolidarityLeaderApplicants(String stockCode,
        Long solidarityLeaderElectionId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : 1, \"nickname\" : \"양군짜앙\", \"profileImageUrl\" : \"https://profile-image-url\", \"individualStockCountLabel\" : \"1억+\", \"commentsForStockHolder\" : \"안녕하세요. 잘 이끌겠습니다. 뽑아주십쇼.\", \"solidarityApplicantId\" : 20 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/solidarity-leader-applicants/{solidarityLeaderApplicantId} : 주주대표 지원서 조회하기
     * 임시저장 상태의 경우 작성자만 조회 가능하고, 제출 완료 상태의 경우 주주라면 조회 가능합니다.
     *
     * @param stockCode Stock Code (required)
     * @param solidarityLeaderElectionId Solidarity Leader Election ID (required)
     * @param solidarityLeaderApplicantId Solidarity Leader Applicant ID (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see SolidarityLeaderElectionApi#getSolidarityLeaderApplication
     */
    default ResponseEntity<SolidarityLeaderApplicationResponse> getSolidarityLeaderApplication(String stockCode,
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
     * GET /api/solidarity-leader-elections/procedures : 선출 과정 안내 조회
     * 선출 과정 안내 리스트를 조회한다.
     *
     * @return Successful response (status code 200)
     * @see SolidarityLeaderElectionApi#getSolidarityLeaderElectionProcedures
     */
    default ResponseEntity<SolidarityLeaderElectionProceduresDataResponse> getSolidarityLeaderElectionProcedures() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"durationDays\" : 6, \"name\" : \"name\", \"displayOrder\" : 0, \"description\" : \"description\", \"title\" : \"title\" }, { \"durationDays\" : 6, \"name\" : \"name\", \"displayOrder\" : 0, \"description\" : \"description\", \"title\" : \"title\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/solidarity-leader-applicants/{solidarityLeaderApplicantId} : 주주대표 지원서 수정하기 (임시저장 제출)
     *
     * @param stockCode Stock Code (required)
     * @param solidarityLeaderElectionId Solidarity Leader Election Id (required)
     * @param solidarityLeaderApplicantId Solidarity Leader Applicant Id (required)
     * @param solidarityLeaderElectionApplyRequest  (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 403)
     *         or Not Found (status code 404)
     * @see SolidarityLeaderElectionApi#updateSolidarityLeaderApplicant
     */
    default ResponseEntity<SimpleStringResponse> updateSolidarityLeaderApplicant(String stockCode,
        Long solidarityLeaderElectionId,
        Long solidarityLeaderApplicantId,
        SolidarityLeaderElectionApplyRequest solidarityLeaderElectionApplyRequest) {
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
     * DELETE /api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/solidarity-leader-applicants/{solidarityLeaderApplicantId} : 주주대표 지원 취소하기
     * 후보자 선출이 시작되기 전 임시저장 상태 또는 후보자 등록 기간에 취소가 가능합니다. 단일 후보자일 때 취소하게 되면 선출 프로세스가 종료됩니다.
     *
     * @param stockCode Stock Code (required)
     * @param solidarityLeaderElectionId Solidarity Leader Election ID (required)
     * @param solidarityLeaderApplicantId Solidarity Leader Applicant ID (required)
     * @return Successful response (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see SolidarityLeaderElectionApi#withdrawSolidarityLeaderApplication
     */
    default ResponseEntity<SimpleStringResponse> withdrawSolidarityLeaderApplication(String stockCode,
        Long solidarityLeaderElectionId,
        Long solidarityLeaderApplicantId) {
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
