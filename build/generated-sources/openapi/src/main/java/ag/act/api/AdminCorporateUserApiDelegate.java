package ag.act.api;

import ag.act.model.CorporateUserDataResponse;
import ag.act.model.CorporateUserRequest;
import ag.act.model.GetCorporateUserDataResponse;
import ag.act.model.SimpleStringResponse;
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
 * A delegate to be called by the {@link AdminCorporateUserApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminCorporateUserApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/corporate-users : 법인 사업자 생성하기
     *
     * @param corporateUserRequest  (required)
     * @return Successful response (status code 200)
     * @see AdminCorporateUserApi#createCorporateUser
     */
    default ResponseEntity<CorporateUserDataResponse> createCorporateUser(CorporateUserRequest corporateUserRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : \"ID\", \"corporateNo\" : \"법인사업자번호\", \"corporateName\" : \"법인명\", \"status\" : \"상태\", \"leadingSolidarityStockCodes\" : [ \"005930\", \"000930\" ], \"createdAt\" : \"2023-07-13T08:39:14.490Z\", \"updatedAt\" : \"2023-07-13T08:40:08.022Z\", \"deletedAt\" : null } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /api/admin/corporate-users/{corporateId} : 법인 사업자 삭제하기
     *
     * @param corporateId Corporate Id (required)
     * @return Successful (status code 200)
     * @see AdminCorporateUserApi#deleteCorporateUser
     */
    default ResponseEntity<SimpleStringResponse> deleteCorporateUser(Long corporateId) {
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
     * GET /api/admin/corporate-users : 법인 사업자 목록 조회하기
     *
     * @param searchType CorporateUserSearchType (CORPORATE_NAME) (optional)
     * @param searchKeyword Search keyword (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     * @see AdminCorporateUserApi#getCorporateUsers
     */
    default ResponseEntity<GetCorporateUserDataResponse> getCorporateUsers(String searchType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"id\" : \"ID\", \"corporateNo\" : \"법인사업자번호\", \"corporateName\" : \"법인명\", \"status\" : \"상태\", \"leadingSolidarityStockCodes\" : [ \"005930\", \"000930\" ], \"createdAt\" : \"2023-07-13T08:39:14.490Z\", \"updatedAt\" : \"2023-07-13T08:40:08.022Z\", \"deletedAt\" : null }, { \"id\" : \"ID\", \"corporateNo\" : \"법인사업자번호\", \"corporateName\" : \"법인명\", \"status\" : \"상태\", \"leadingSolidarityStockCodes\" : [ \"005930\", \"000930\" ], \"createdAt\" : \"2023-07-13T08:39:14.490Z\", \"updatedAt\" : \"2023-07-13T08:40:08.022Z\", \"deletedAt\" : null } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /api/admin/corporate-users/{corporateId} : 법인 사업자 수정하기
     *
     * @param corporateId Corporate Id (required)
     * @param corporateUserRequest  (required)
     * @return Successful response (status code 200)
     * @see AdminCorporateUserApi#updateCorporateUser
     */
    default ResponseEntity<CorporateUserDataResponse> updateCorporateUser(Long corporateId,
        CorporateUserRequest corporateUserRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : \"ID\", \"corporateNo\" : \"법인사업자번호\", \"corporateName\" : \"법인명\", \"status\" : \"상태\", \"leadingSolidarityStockCodes\" : [ \"005930\", \"000930\" ], \"createdAt\" : \"2023-07-13T08:39:14.490Z\", \"updatedAt\" : \"2023-07-13T08:40:08.022Z\", \"deletedAt\" : null } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
