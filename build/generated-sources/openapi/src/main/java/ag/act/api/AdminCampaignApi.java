/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.CampaignDetailsDataResponse;
import ag.act.model.CreateCampaignRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.GetCampaignsDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UpdateCampaignRequest;
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
public interface AdminCampaignApi {

    default AdminCampaignApiDelegate getDelegate() {
        return new AdminCampaignApiDelegate() {};
    }

    /**
     * POST /api/admin/campaigns : CMS 캠페인 생성
     *
     * @param createCampaignRequest  (required)
     * @return Successful response (status code 200)
     *         or Board group of the category does not match (status code 400)
     *         or Not Found (status code 404)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/campaigns",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<CampaignDetailsDataResponse> createCampaign(
         @Valid @RequestBody CreateCampaignRequest createCampaignRequest
    ) {
        return getDelegate().createCampaign(createCampaignRequest);
    }


    /**
     * POST /api/admin/campaigns/{campaignId}/zip-file-request : 캠페인 전체 전자문서 ZIP 파일 생성요청
     *
     * @param campaignId Campaign document id parameter (required)
     * @param isSecured ZIP file is secured with password or not (optional, default to true)
     * @return Successful response (status code 200)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/campaigns/{campaignId}/zip-file-request",
        produces = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> createCampaignDigitalDocumentZipFile(
         @PathVariable("campaignId") Long campaignId,
         @Valid @RequestParam(value = "isSecured", required = false, defaultValue = "true") Boolean isSecured
    ) {
        return getDelegate().createCampaignDigitalDocumentZipFile(campaignId, isSecured);
    }


    /**
     * DELETE /api/admin/campaigns/{campaignId} : CMS 캠페인 삭제
     *
     * @param campaignId campaign id parameter (required)
     * @return Successful response (status code 200)
     *         or Not Found. (status code 400)
     */
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/api/admin/campaigns/{campaignId}",
        produces = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> deleteCampaign(
         @PathVariable("campaignId") Long campaignId
    ) {
        return getDelegate().deleteCampaign(campaignId);
    }


    /**
     * POST /api/admin/campaigns/{campaignId}/csv-download : 캠페인 내의 전자문서 혹은 설문 응답내역 엑셀 파일 다운로드
     *
     * @param campaignId Campaign document id parameter (required)
     * @return Campaign Document Files download (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/campaigns/{campaignId}/csv-download",
        produces = { "application/octet-stream", "application/json" }
    )
    default ResponseEntity<org.springframework.core.io.Resource> downloadCampaignUserResponseInCsv(
         @PathVariable("campaignId") Long campaignId
    ) {
        return getDelegate().downloadCampaignUserResponseInCsv(campaignId);
    }


    /**
     * GET /api/admin/campaigns/{campaignId} : CMS 캠페인 상세조회
     *
     * @param campaignId campaign id parameter (required)
     * @return Successful response (status code 200)
     *         or Not Found. (status code 400)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/campaigns/{campaignId}",
        produces = { "application/json" }
    )
    default ResponseEntity<CampaignDetailsDataResponse> getCampaignDetailsAdmin(
         @PathVariable("campaignId") Long campaignId
    ) {
        return getDelegate().getCampaignDetailsAdmin(campaignId);
    }


    /**
     * GET /api/admin/campaigns : 캠페인 목록 조회하기
     *
     * @param searchType CampaignSearchType (TITLE, STOCK_GROUP_NAME) (optional)
     * @param searchKeyword Search keyword for title for stock (optional)
     * @param boardCategory BoardCategory action surveys and etc (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/campaigns",
        produces = { "application/json" }
    )
    default ResponseEntity<GetCampaignsDataResponse> getCampaigns(
         @Valid @RequestParam(value = "searchType", required = false) String searchType,
         @Valid @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
         @Valid @RequestParam(value = "boardCategory", required = false) String boardCategory,
         @Valid @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
         @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
         @Valid @RequestParam(value = "sorts", required = false, defaultValue = "createdAt:desc") List<String> sorts
    ) {
        return getDelegate().getCampaigns(searchType, searchKeyword, boardCategory, page, size, sorts);
    }


    /**
     * PATCH /api/admin/campaigns/{campaignId} : CMS 캠페인 수정
     *
     * @param campaignId campaign id parameter (required)
     * @param updateCampaignRequest  (required)
     * @return Successful response (status code 200)
     *         or Not Found. (status code 400)
     */
    @RequestMapping(
        method = RequestMethod.PATCH,
        value = "/api/admin/campaigns/{campaignId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<CampaignDetailsDataResponse> updateCampaign(
         @PathVariable("campaignId") Long campaignId,
         @Valid @RequestBody UpdateCampaignRequest updateCampaignRequest
    ) {
        return getDelegate().updateCampaign(campaignId, updateCampaignRequest);
    }

}
