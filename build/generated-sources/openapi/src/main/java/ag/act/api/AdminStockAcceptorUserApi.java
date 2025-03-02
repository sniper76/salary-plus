/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.CreateStockAcceptorUserRequest;
import ag.act.model.DeleteStockAcceptorUserRequest;
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
public interface AdminStockAcceptorUserApi {

    default AdminStockAcceptorUserApiDelegate getDelegate() {
        return new AdminStockAcceptorUserApiDelegate() {};
    }

    /**
     * POST /api/admin/stocks/{code}/acceptor-users : CMS 종목 상세 수임인 선정
     *
     * @param code Stock Code (required)
     * @param createStockAcceptorUserRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/stocks/{code}/acceptor-users",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> createStockAcceptorUser(
         @PathVariable("code") String code,
         @Valid @RequestBody CreateStockAcceptorUserRequest createStockAcceptorUserRequest
    ) {
        return getDelegate().createStockAcceptorUser(code, createStockAcceptorUserRequest);
    }


    /**
     * DELETE /api/admin/stocks/{code}/acceptor-users : CMS 종목 상세 수임인 해임
     *
     * @param code Stock Code (required)
     * @param deleteStockAcceptorUserRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/api/admin/stocks/{code}/acceptor-users",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> deleteStockAcceptorUser(
         @PathVariable("code") String code,
         @Valid @RequestBody DeleteStockAcceptorUserRequest deleteStockAcceptorUserRequest
    ) {
        return getDelegate().deleteStockAcceptorUser(code, deleteStockAcceptorUserRequest);
    }

}
