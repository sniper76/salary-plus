/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.CreateStockReferenceDateRequest;
import ag.act.model.DigitalDocumentUserDetailsDataResponse;
import ag.act.model.ErrorResponse;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.StockReferenceDateDataResponse;
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
public interface AdminDigitalDocumentApi {

    default AdminDigitalDocumentApiDelegate getDelegate() {
        return new AdminDigitalDocumentApiDelegate() {};
    }

    /**
     * POST /api/admin/digital-document/{digitalDocumentId}/zip-file-request : 전자문서 ZIP 파일 생성요청
     *
     * @param digitalDocumentId Digital document id parameter (required)
     * @param isSecured ZIP file is secured with password or not (optional, default to true)
     * @return Successful response (status code 200)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/digital-document/{digitalDocumentId}/zip-file-request",
        produces = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> createDigitalDocumentZipFile(
         @PathVariable("digitalDocumentId") Long digitalDocumentId,
         @Valid @RequestParam(value = "isSecured", required = false, defaultValue = "true") Boolean isSecured
    ) {
        return getDelegate().createDigitalDocumentZipFile(digitalDocumentId, isSecured);
    }


    /**
     * POST /api/admin/digital-document/{digitalDocumentId}/csv-download : 전자문서 응답내역 엑셀 파일 다운로드
     *
     * @param digitalDocumentId Digital document id parameter (required)
     * @return Digital Document Files download successfully (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     *         or Internal Server Error (status code 500)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/digital-document/{digitalDocumentId}/csv-download",
        produces = { "application/octet-stream", "application/json" }
    )
    default ResponseEntity<org.springframework.core.io.Resource> downloadDigitalDocumentUserResponseInCsv(
         @PathVariable("digitalDocumentId") Long digitalDocumentId
    ) {
        return getDelegate().downloadDigitalDocumentUserResponseInCsv(digitalDocumentId);
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
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/api/admin/digital-document/{digitalDocumentId}/users",
        produces = { "application/json" }
    )
    default ResponseEntity<DigitalDocumentUserDetailsDataResponse> getDigitalDocumentUsers(
         @PathVariable("digitalDocumentId") Long digitalDocumentId,
         @Valid @RequestParam(value = "searchType", required = false) String searchType,
         @Valid @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
         @Valid @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
         @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
         @Valid @RequestParam(value = "sorts", required = false, defaultValue = "name:asc") List<String> sorts
    ) {
        return getDelegate().getDigitalDocumentUsers(digitalDocumentId, searchType, searchKeyword, page, size, sorts);
    }


    /**
     * POST /api/admin/digital-document/preview : 전자문서 미리보기
     *
     * @param previewDigitalDocumentRequest  (optional)
     * @return Preview Digital Document Files downloaded successfully (status code 200)
     *         or Bad Request. Invalid file format or data. (status code 400)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/admin/digital-document/preview",
        produces = { "application/octet-stream", "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<org.springframework.core.io.Resource> previewDigitalDocument(
         @Valid @RequestBody(required = false) PreviewDigitalDocumentRequest previewDigitalDocumentRequest
    ) {
        return getDelegate().previewDigitalDocument(previewDigitalDocumentRequest);
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
     */
    @RequestMapping(
        method = RequestMethod.PATCH,
        value = "/api/admin/digital-document/{digitalDocumentId}/reference-dates/{referenceDateId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<StockReferenceDateDataResponse> updateDigitalDocumentReferenceDate(
         @PathVariable("digitalDocumentId") Long digitalDocumentId,
         @PathVariable("referenceDateId") Long referenceDateId,
         @Valid @RequestBody CreateStockReferenceDateRequest createStockReferenceDateRequest
    ) {
        return getDelegate().updateDigitalDocumentReferenceDate(digitalDocumentId, referenceDateId, createStockReferenceDateRequest);
    }

}
