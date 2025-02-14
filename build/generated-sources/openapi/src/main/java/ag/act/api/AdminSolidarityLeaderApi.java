/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.CreateSolidarityLeaderForCorporateUserRequest;
import ag.act.model.CreateSolidarityLeaderRequest;
import ag.act.model.DismissSolidarityLeaderRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UpdateSolidarityLeaderMessageRequest;
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
public interface AdminSolidarityLeaderApi {

    default AdminSolidarityLeaderApiDelegate getDelegate() {
        return new AdminSolidarityLeaderApiDelegate() {};
    }

    /**
     * POST /api/admin/solidarity-leaders/{solidarityId} : CMS 종목 주주대표 선정하기
     *
     * @param solidarityId Solidarity Id (required)
     * @param createSolidarityLeaderRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/solidarity-leaders/{solidarityId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> createSolidarityLeader(
         @PathVariable("solidarityId") Long solidarityId,
         @Valid @RequestBody CreateSolidarityLeaderRequest createSolidarityLeaderRequest
    ) {
        return getDelegate().createSolidarityLeader(solidarityId, createSolidarityLeaderRequest);
    }


    /**
     * POST /api/admin/solidarity-leaders/{solidarityId}/corporate-user : CMS 법인사업자 주주대표 선정하기
     *
     * @param solidarityId Solidarity Id (required)
     * @param createSolidarityLeaderForCorporateUserRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/solidarity-leaders/{solidarityId}/corporate-user",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> createSolidarityLeaderForCorporateUser(
         @PathVariable("solidarityId") Long solidarityId,
         @Valid @RequestBody CreateSolidarityLeaderForCorporateUserRequest createSolidarityLeaderForCorporateUserRequest
    ) {
        return getDelegate().createSolidarityLeaderForCorporateUser(solidarityId, createSolidarityLeaderForCorporateUserRequest);
    }


    /**
     * DELETE /api/admin/solidarity-leaders/{solidarityId} : CMS 종목 주주대표 해임하기
     *
     * @param solidarityId Solidarity Id (required)
     * @param dismissSolidarityLeaderRequest  (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/api/admin/solidarity-leaders/{solidarityId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> dismissSolidarityLeader(
         @PathVariable("solidarityId") Long solidarityId,
         @Valid @RequestBody DismissSolidarityLeaderRequest dismissSolidarityLeaderRequest
    ) {
        return getDelegate().dismissSolidarityLeader(solidarityId, dismissSolidarityLeaderRequest);
    }


    /**
     * PATCH /api/admin/solidarity-leaders/{solidarityId}/message : CMS 주주대표 한마디 수정하기
     *
     * @param solidarityId Solidarity ID parameter (required)
     * @param updateSolidarityLeaderMessageRequest  (required)
     * @return Successful response (status code 200)
     *         or Inappropriate Message. (status code 400)
     *         or No Solidarity Leader Found (status code 404)
     */
    @RequestMapping(
        method = RequestMethod.PATCH,
        value = "/api/admin/solidarity-leaders/{solidarityId}/message",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> updateSolidarityLeaderMessageByAdmin(
         @PathVariable("solidarityId") Long solidarityId,
         @Valid @RequestBody UpdateSolidarityLeaderMessageRequest updateSolidarityLeaderMessageRequest
    ) {
        return getDelegate().updateSolidarityLeaderMessageByAdmin(solidarityId, updateSolidarityLeaderMessageRequest);
    }

}
