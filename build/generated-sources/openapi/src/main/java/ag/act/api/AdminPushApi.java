/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.CreatePushRequest;
import ag.act.model.GetPushDataResponse;
import ag.act.model.PushDetailsDataResponse;
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
public interface AdminPushApi {

    default AdminPushApiDelegate getDelegate() {
        return new AdminPushApiDelegate() {};
    }

    /**
     * POST /api/admin/pushes : 푸시 등록 생성하기
     *
     * @param createPushRequest  (required)
     * @return Successful response (status code 200)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/pushes",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<PushDetailsDataResponse> createPushAdmin(
         @Valid @RequestBody CreatePushRequest createPushRequest
    ) {
        return getDelegate().createPushAdmin(createPushRequest);
    }


    /**
     * DELETE /api/admin/pushes/{pushId} : 푸시 예약 삭제하기
     *
     * @param pushId Push ID (required)
     * @return Successful (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     */
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/api/admin/pushes/{pushId}",
        produces = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> deletePushAdmin(
         @PathVariable("pushId") Long pushId
    ) {
        return getDelegate().deletePushAdmin(pushId);
    }


    /**
     * GET /api/admin/automated-pushes : 개인 자동 푸시 목록 조회하기
     *
     * @param searchType SearchType (PUSH_TITLE / AUTHOR_NAME / AUTHOR_NICKNAME / PUSH_CONTENT) (optional)
     * @param searchKeyword Search keyword (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/automated-pushes",
        produces = { "application/json" }
    )
    default ResponseEntity<GetPushDataResponse> getAutomatedPushesAdmin(
         @Valid @RequestParam(value = "searchType", required = false) String searchType,
         @Valid @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
         @Valid @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
         @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
         @Valid @RequestParam(value = "sorts", required = false, defaultValue = "createdAt:desc") List<String> sorts
    ) {
        return getDelegate().getAutomatedPushesAdmin(searchType, searchKeyword, page, size, sorts);
    }


    /**
     * GET /api/admin/pushes/{pushId} : 푸시 정보 상세조회
     *
     * @param pushId Push ID (required)
     * @return Successful (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/pushes/{pushId}",
        produces = { "application/json" }
    )
    default ResponseEntity<PushDetailsDataResponse> getPushDetailsAdmin(
         @PathVariable("pushId") Long pushId
    ) {
        return getDelegate().getPushDetailsAdmin(pushId);
    }


    /**
     * GET /api/admin/pushes : 푸시 목록 조회하기
     *
     * @param searchType SearchType (PUSH_TITLE / STOCK_NAME / PUSH_CONTENT) (optional)
     * @param searchKeyword Search keyword (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/pushes",
        produces = { "application/json" }
    )
    default ResponseEntity<GetPushDataResponse> getPushesAdmin(
         @Valid @RequestParam(value = "searchType", required = false) String searchType,
         @Valid @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
         @Valid @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
         @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
         @Valid @RequestParam(value = "sorts", required = false, defaultValue = "createdAt:desc") List<String> sorts
    ) {
        return getDelegate().getPushesAdmin(searchType, searchKeyword, page, size, sorts);
    }

}
