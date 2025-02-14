package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UserDigitalDocumentResponse;
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
 * A delegate to be called by the {@link UserDigitalDocumentApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface UserDigitalDocumentApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/users/digital-document/{digitalDocumentId} : 유저의 전자문서 서명하기
     *
     * @param digitalDocumentId Digital Document ID parameter (required)
     * @param signImage 서명 이미지 (optional)
     * @param idCardImage 신분증 이미지 (optional)
     * @param bankAccountImages  (optional)
     * @param hectoEncryptedBankAccountPdf 암호화된 헥토측 잔고증명서 pdf 파일 (optional)
     * @param answerData  (optional)
     * @return Digital Document Files uploaded successfully (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see UserDigitalDocumentApi#createUserDigitalDocumentWithImage
     */
    default ResponseEntity<UserDigitalDocumentResponse> createUserDigitalDocumentWithImage(Long digitalDocumentId,
        MultipartFile signImage,
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImages,
        MultipartFile hectoEncryptedBankAccountPdf,
        String answerData) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"id\" : \"digitalDocumentId\", \"answerStatus\" : \"SAVE or COMPLETE\", \"digitalDocumentType\" : \"DIGITAL_PROXY or JOINT_OWNERSHIP_DOCUMENT or ETC_DOCUMENT\", \"joinUserCount\" : \"전자문서 참여자수\", \"shareholdingRatio\" : \"지분율\", \"targetStartDate\" : \"전자문서 시작일자\", \"targetEndDate\" : \"전자문서 종료일자\", \"user\" : \"전자문서 위임인 정보\", \"stock\" : \"전자문서 위임인 주식정보\", \"acceptUser\" : \"전자문서 수임인 정보\", \"items\" : \"전자문서 위임장 찬성/반대/기권 기본 선택정보\", \"attachOptions\" : \"전자문서 첨부 옵션\", \"digitalDocumentDownload\" : \"전자문서 다운로드 정보\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /api/users/digital-document/{digitalDocumentId} : 전자문서 사용자별 삭제
     *
     * @param digitalDocumentId Digital Document ID parameter (required)
     * @return Digital Document delete successfully (status code 200)
     *         or Bad Request. Invalid data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see UserDigitalDocumentApi#deleteUserDigitalDocument
     */
    default ResponseEntity<SimpleStringResponse> deleteUserDigitalDocument(Long digitalDocumentId) {
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
     * GET /api/users/digital-document/{digitalDocumentId}/download-document : 전자문서 사용자별 pdf 다운로드
     *
     * @param digitalDocumentId Digital Document ID parameter (required)
     * @return Digital Document Files downloaded successfully (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see UserDigitalDocumentApi#getUserDigitalDocumentPdf
     */
    default ResponseEntity<org.springframework.core.io.Resource> getUserDigitalDocumentPdf(Long digitalDocumentId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /api/users/digital-document/{digitalDocumentId} : 전자문서 사용자별 제출 상태로 변경
     *
     * @param digitalDocumentId Digital Document ID parameter (required)
     * @return Digital Document Update successfully (status code 200)
     *         or Bad Request. Invalid data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see UserDigitalDocumentApi#updateUserDigitalDocumentStatus
     */
    default ResponseEntity<SimpleStringResponse> updateUserDigitalDocumentStatus(Long digitalDocumentId) {
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
