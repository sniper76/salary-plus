package ag.act.api;

import ag.act.model.CreateStockReferenceDateRequest;
import ag.act.model.DigitalDocumentUserDetailsDataResponse;
import ag.act.model.ErrorResponse;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.StockReferenceDateDataResponse;
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
 * A delegate to be called by the {@link AdminDigitalDocumentApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminDigitalDocumentApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/digital-document/{digitalDocumentId}/zip-file-request : 전자문서 ZIP 파일 생성요청
     *
     * @param digitalDocumentId Digital document id parameter (required)
     * @param isSecured ZIP file is secured with password or not (optional, default to true)
     * @return Successful response (status code 200)
     * @see AdminDigitalDocumentApi#createDigitalDocumentZipFile
     */
    default ResponseEntity<SimpleStringResponse> createDigitalDocumentZipFile(Long digitalDocumentId,
        Boolean isSecured) {
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
     * POST /api/admin/digital-document/{digitalDocumentId}/csv-download : 전자문서 응답내역 엑셀 파일 다운로드
     *
     * @param digitalDocumentId Digital document id parameter (required)
     * @return Digital Document Files download successfully (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see AdminDigitalDocumentApi#downloadDigitalDocumentUserResponseInCsv
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadDigitalDocumentUserResponseInCsv(Long digitalDocumentId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/digital-document/{digitalDocumentId}/users : 전자문서 유저 응답내역 리스트 조회
     *
     * @param digitalDocumentId Digital document ID parameter (required)
     * @param searchType UserSearchType - NAME(이름) (optional)
     * @param searchKeyword Search keyword (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to name:asc)
     * @return Successful response (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see AdminDigitalDocumentApi#getDigitalDocumentUsers
     */
    default ResponseEntity<DigitalDocumentUserDetailsDataResponse> getDigitalDocumentUsers(Long digitalDocumentId,
        String searchType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ null, null ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/admin/digital-document/preview : 전자문서 미리보기
     *
     * @param previewDigitalDocumentRequest  (optional)
     * @return Preview Digital Document Files downloaded successfully (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     * @see AdminDigitalDocumentApi#previewDigitalDocument
     */
    default ResponseEntity<org.springframework.core.io.Resource> previewDigitalDocument(PreviewDigitalDocumentRequest previewDigitalDocumentRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /api/admin/digital-document/{digitalDocumentId}/reference-dates/{referenceDateId} : 액션 리스트 전자문서 위임장 기준일 변경
     *
     * @param digitalDocumentId Digital Document ID parameter (required)
     * @param referenceDateId ReferenceDate ID parameter (required)
     * @param createStockReferenceDateRequest  (required)
     * @return Successful response (status code 200)
     *         or Bad Request. Invalid data. (status code 400)
     *         or Internal Server Error (status code 500)
     * @see AdminDigitalDocumentApi#updateDigitalDocumentReferenceDate
     */
    default ResponseEntity<StockReferenceDateDataResponse> updateDigitalDocumentReferenceDate(Long digitalDocumentId,
        Long referenceDateId,
        CreateStockReferenceDateRequest createStockReferenceDateRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : 1, \"stockCode\" : \"145020\", \"referenceDate\" : \"2023-08-10\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
