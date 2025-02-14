/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.ErrorResponse;
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
public interface UserRetentionWeeklyMatrixByDigitalDocumentApi {

    default UserRetentionWeeklyMatrixByDigitalDocumentApiDelegate getDelegate() {
        return new UserRetentionWeeklyMatrixByDigitalDocumentApiDelegate() {};
    }

    /**
     * GET /api/admin/digital-documents/{digitalDocumentId}/pin-verification-given-register-digital-document-not-complete-retention-download : 전자문서 서명 기간 내의 신규 가입자 중 해당 전자문서에 참여하지 않은 사용자들의 리텐션 csv 다운로드
     *
     * @param digitalDocumentId Digital document id parameter (required)
     * @return download retention of user that register in digital document progress period and not participate digital document (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/digital-documents/{digitalDocumentId}/pin-verification-given-register-digital-document-not-complete-retention-download",
        produces = { "application/octet-stream", "application/json" }
    )
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationGivenRegisterAndDigitalDocumentNotCompleteRetentionDownload(
         @PathVariable("digitalDocumentId") Long digitalDocumentId
    ) {
        return getDelegate().downloadPinVerificationGivenRegisterAndDigitalDocumentNotCompleteRetentionDownload(digitalDocumentId);
    }


    /**
     * GET /api/admin/digital-documents/{digitalDocumentId}/pin-verification-given-register-digital-document-complete-retention-download : 전자문서 서명 기간동안 신규 가입한 사용자의 리텐션 조회
     *
     * @param digitalDocumentId Digital document id parameter (required)
     * @return Digital Document Files download successfully (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/digital-documents/{digitalDocumentId}/pin-verification-given-register-digital-document-complete-retention-download",
        produces = { "application/octet-stream", "application/json" }
    )
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationGivenRegisterDigitalDocumentCompleteRetentionDownload(
         @PathVariable("digitalDocumentId") Long digitalDocumentId
    ) {
        return getDelegate().downloadPinVerificationGivenRegisterDigitalDocumentCompleteRetentionDownload(digitalDocumentId);
    }


    /**
     * GET /api/admin/campaigns/{campaignId}/pin-verification-given-register-digital-document-complete-retention-download : 캠페인 기간 내의 신규 가입자 중 해당 캠페인에 참여한 사용자들의 리텐션 csv 다운로드
     *
     * @param campaignId Campaign document id parameter (required)
     * @return download retention of user that register and participate campaign in campaign period (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/campaigns/{campaignId}/pin-verification-given-register-digital-document-complete-retention-download",
        produces = { "application/octet-stream", "application/json" }
    )
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationGivenRegisterDigitalDocumentOfCampaignCompleteRetentionDownload(
         @PathVariable("campaignId") Long campaignId
    ) {
        return getDelegate().downloadPinVerificationGivenRegisterDigitalDocumentOfCampaignCompleteRetentionDownload(campaignId);
    }


    /**
     * GET /api/admin/campaigns/{campaignId}/pin-verification-given-register-digital-document-not-complete-retention-download : 캠페인 기간 내의 신규 가입자 중 해당 캠페인에 참여하지 않은 사용자들의 리텐션 csv 다운로드
     *
     * @param campaignId Campaign document id parameter (required)
     * @return download retention of user that register in campaign progress period and not participate in the campaign (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/campaigns/{campaignId}/pin-verification-given-register-digital-document-not-complete-retention-download",
        produces = { "application/octet-stream", "application/json" }
    )
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationGivenRegisterDigitalDocumentOfCampaignNotCompleteRetentionDownload(
         @PathVariable("campaignId") Long campaignId
    ) {
        return getDelegate().downloadPinVerificationGivenRegisterDigitalDocumentOfCampaignNotCompleteRetentionDownload(campaignId);
    }

}
