package ag.act.api;

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
 * A delegate to be called by the {@link UserRetentionWeeklyMatrixByDigitalDocumentApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface UserRetentionWeeklyMatrixByDigitalDocumentApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/admin/digital-documents/{digitalDocumentId}/pin-verification-given-register-digital-document-not-complete-retention-download : 전자문서 서명 기간 내의 신규 가입자 중 해당 전자문서에 참여하지 않은 사용자들의 리텐션 csv 다운로드
     *
     * @param digitalDocumentId Digital document id parameter (required)
     * @return download retention of user that register in digital document progress period and not participate digital document (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see UserRetentionWeeklyMatrixByDigitalDocumentApi#downloadPinVerificationGivenRegisterAndDigitalDocumentNotCompleteRetentionDownload
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationGivenRegisterAndDigitalDocumentNotCompleteRetentionDownload(Long digitalDocumentId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/digital-documents/{digitalDocumentId}/pin-verification-given-register-digital-document-complete-retention-download : 전자문서 서명 기간동안 신규 가입한 사용자의 리텐션 조회
     *
     * @param digitalDocumentId Digital document id parameter (required)
     * @return Digital Document Files download successfully (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see UserRetentionWeeklyMatrixByDigitalDocumentApi#downloadPinVerificationGivenRegisterDigitalDocumentCompleteRetentionDownload
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationGivenRegisterDigitalDocumentCompleteRetentionDownload(Long digitalDocumentId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/campaigns/{campaignId}/pin-verification-given-register-digital-document-complete-retention-download : 캠페인 기간 내의 신규 가입자 중 해당 캠페인에 참여한 사용자들의 리텐션 csv 다운로드
     *
     * @param campaignId Campaign document id parameter (required)
     * @return download retention of user that register and participate campaign in campaign period (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see UserRetentionWeeklyMatrixByDigitalDocumentApi#downloadPinVerificationGivenRegisterDigitalDocumentOfCampaignCompleteRetentionDownload
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationGivenRegisterDigitalDocumentOfCampaignCompleteRetentionDownload(Long campaignId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/campaigns/{campaignId}/pin-verification-given-register-digital-document-not-complete-retention-download : 캠페인 기간 내의 신규 가입자 중 해당 캠페인에 참여하지 않은 사용자들의 리텐션 csv 다운로드
     *
     * @param campaignId Campaign document id parameter (required)
     * @return download retention of user that register in campaign progress period and not participate in the campaign (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see UserRetentionWeeklyMatrixByDigitalDocumentApi#downloadPinVerificationGivenRegisterDigitalDocumentOfCampaignNotCompleteRetentionDownload
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationGivenRegisterDigitalDocumentOfCampaignNotCompleteRetentionDownload(Long campaignId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
