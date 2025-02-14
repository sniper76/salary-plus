/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.CorporateUserDataResponse;
import ag.act.model.CorporateUserRequest;
import ag.act.model.GetCorporateUserDataResponse;
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
public interface AdminCorporateUserApi {

    default AdminCorporateUserApiDelegate getDelegate() {
        return new AdminCorporateUserApiDelegate() {};
    }

    /**
     * POST /api/admin/corporate-users : 법인 사업자 생성하기
     *
     * @param corporateUserRequest  (required)
     * @return Successful response (status code 200)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/corporate-users",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<CorporateUserDataResponse> createCorporateUser(
         @Valid @RequestBody CorporateUserRequest corporateUserRequest
    ) {
        return getDelegate().createCorporateUser(corporateUserRequest);
    }


    /**
     * DELETE /api/admin/corporate-users/{corporateId} : 법인 사업자 삭제하기
     *
     * @param corporateId Corporate Id (required)
     * @return Successful (status code 200)
     */
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/api/admin/corporate-users/{corporateId}",
        produces = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> deleteCorporateUser(
         @PathVariable("corporateId") Long corporateId
    ) {
        return getDelegate().deleteCorporateUser(corporateId);
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
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/corporate-users",
        produces = { "application/json" }
    )
    default ResponseEntity<GetCorporateUserDataResponse> getCorporateUsers(
         @Valid @RequestParam(value = "searchType", required = false) String searchType,
         @Valid @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
         @Valid @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
         @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
         @Valid @RequestParam(value = "sorts", required = false, defaultValue = "createdAt:desc") List<String> sorts
    ) {
        return getDelegate().getCorporateUsers(searchType, searchKeyword, page, size, sorts);
    }


    /**
     * PATCH /api/admin/corporate-users/{corporateId} : 법인 사업자 수정하기
     *
     * @param corporateId Corporate Id (required)
     * @param corporateUserRequest  (required)
     * @return Successful response (status code 200)
     */
    @RequestMapping(
        method = RequestMethod.PATCH,
        value = "/api/admin/corporate-users/{corporateId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<CorporateUserDataResponse> updateCorporateUser(
         @PathVariable("corporateId") Long corporateId,
         @Valid @RequestBody CorporateUserRequest corporateUserRequest
    ) {
        return getDelegate().updateCorporateUser(corporateId, corporateUserRequest);
    }

}
