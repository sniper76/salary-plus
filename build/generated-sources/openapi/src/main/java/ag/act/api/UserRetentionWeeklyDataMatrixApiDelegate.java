package ag.act.api;

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
 * A delegate to be called by the {@link UserRetentionWeeklyDataMatrixApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface UserRetentionWeeklyDataMatrixApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/admin/data-matrices/user-retention-weekly/digital-document-participation-rate-given-register : 특정 주차에 가입한 사람들에 대해, 전자문서에 참여한 사람의 비율
     *
     * @return User Retention Weekly Matrix File download successfully (status code 200)
     * @see UserRetentionWeeklyDataMatrixApi#downloadDigitalDocumentParticipationRateGivenRegisterCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadDigitalDocumentParticipationRateGivenRegisterCsv() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/data-matrices/user-retention-weekly/non-retained-user-given-register-and-digital-document-complete : 특정 주차에 가입하고 전자문서에 참여한 사람들에 대해, 각 주에 접속한 적 없는 유저의 수
     *
     * @return User Retention Weekly Matrix File download successfully (status code 200)
     * @see UserRetentionWeeklyDataMatrixApi#downloadNonRetainedUserGivenRegisterAndDigitalDocumentCompleteCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadNonRetainedUserGivenRegisterAndDigitalDocumentCompleteCsv() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/data-matrices/user-retention-weekly/non-retained-user-given-register-and-no-digital-document-complete : 특정 주차에 가입하고 전자문서에 참여한 적 없는 사람들에 대해, 각 주에 접속한 적 없는 유저의 수
     *
     * @return User Retention Weekly Matrix File download successfully (status code 200)
     * @see UserRetentionWeeklyDataMatrixApi#downloadNonRetainedUserGivenRegisterAndNoDigitalDocumentCompleteCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadNonRetainedUserGivenRegisterAndNoDigitalDocumentCompleteCsv() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/data-matrices/user-retention-weekly/pin-verification-for-three-weeks-in-a-row-given-register-and-digital-document-complete : 특정 주차에 가입하고 전자문서에 참여한 적 있는 사람들에 대해, 3주 연속 접속한 유저의 수
     *
     * @return User Retention Weekly Matrix File download successfully (status code 200)
     * @see UserRetentionWeeklyDataMatrixApi#downloadPinVerificationForThreeWeeksForThreeWeeksInARowGivenRegisterAndDigitalDocumentCompleteCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationForThreeWeeksForThreeWeeksInARowGivenRegisterAndDigitalDocumentCompleteCsv() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/data-matrices/user-retention-weekly/pin-verification-for-three-weeks-in-a-row-given-register-and-no-digital-document-complete : 특정 주차에 가입하고 전자문서에 참여한 적 없는 사람들에 대해, 3주 연속 접속한 유저의 수
     *
     * @return User Retention Weekly Matrix File download successfully (status code 200)
     * @see UserRetentionWeeklyDataMatrixApi#downloadPinVerificationForThreeWeeksForThreeWeeksInARowGivenRegisterAndNoDigitalDocumentCompleteCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationForThreeWeeksForThreeWeeksInARowGivenRegisterAndNoDigitalDocumentCompleteCsv() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/data-matrices/user-retention-weekly/pin-verification-given-first-digital-document-complete : 특정 주차에 처음으로 전자문서에 참여한 사람들에 대해, 이후 핀 로그인 횟수 추이
     *
     * @return User Retention Weekly Matrix File download successfully (status code 200)
     * @see UserRetentionWeeklyDataMatrixApi#downloadPinVerificationGivenFirstDigitalDocumentCompleteCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationGivenFirstDigitalDocumentCompleteCsv() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/data-matrices/user-retention-weekly/pin-verification-given-register-and-digital-document-complete : 특정 주차에 가입하고 전자문서에도 참여한 사람들에 대해, 이후 핀 로그인 횟수 추이
     *
     * @return User Retention Weekly Matrix File download successfully (status code 200)
     * @see UserRetentionWeeklyDataMatrixApi#downloadPinVerificationGivenRegisterAndDigitalDocumentCompleteCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationGivenRegisterAndDigitalDocumentCompleteCsv() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/data-matrices/user-retention-weekly/pin-verification-given-register-and-no-digital-document-complete : 특정 주차에 가입하고 전자문서에 참여한 적 없는 사람들에 대해, 이후 핀 로그인 횟수 추이
     *
     * @return User Retention Weekly Matrix File download successfully (status code 200)
     * @see UserRetentionWeeklyDataMatrixApi#downloadPinVerificationGivenRegisterAndNoDigitalDocumentCompleteCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationGivenRegisterAndNoDigitalDocumentCompleteCsv() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/data-matrices/user-retention-weekly/pin-verification-given-register : 특정 주차에 가입한 사람들에 대해, 이후 핀 로그인 횟수 추이
     *
     * @return User Retention Weekly Matrix File download successfully (status code 200)
     * @see UserRetentionWeeklyDataMatrixApi#downloadPinVerificationGivenRegisterCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadPinVerificationGivenRegisterCsv() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/data-matrices/user-retention-weekly/{formattedCsvDataType} : DownloadCsv_모든 유저별 주차별 리텐션|전자문서참여기회지표|전자문서참여기회지표(기타문서X) 다운로드
     *
     * @param formattedCsvDataType User Retention Weekly CSV Data Type (converted LowerCase and &#39;_&#39;) (required)
     * @return User Retention Weekly Matrix File download successfully (status code 200)
     * @see UserRetentionWeeklyDataMatrixApi#downloadUserRetentionWeeklyCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadUserRetentionWeeklyCsv(String formattedCsvDataType) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
