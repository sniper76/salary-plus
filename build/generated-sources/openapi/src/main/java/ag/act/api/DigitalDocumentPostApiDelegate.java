package ag.act.api;

import ag.act.model.GetPostDigitalDocumentResponse;
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
 * A delegate to be called by the {@link DigitalDocumentPostApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface DigitalDocumentPostApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/posts/digital-document/{digitalDocumentType} : 전자문서 의결권위임 리스트 조회
     *
     * @param digitalDocumentType Digital Document Type parameter (DIGITAL_PROXY) (required)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     * @see DigitalDocumentPostApi#getDigitalDocumentPosts
     */
    default ResponseEntity<GetPostDigitalDocumentResponse> getDigitalDocumentPosts(String digitalDocumentType,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"id\" : 0, \"title\" : \"title\", \"stock\" : { \"code\" : \"종목코드\", \"name\" : \"종목명\", \"totalIssuedQuantity\" : \"상장주식수\", \"representativePhoneNumber\" : \"(02) 555-6666\", \"memberCount\" : \"주주수\", \"stake\" : \"지분율\" }, \"content\" : \"content\", \"digitalDocument\" : { \"id\" : \"digitalDocumentId\", \"answerStatus\" : \"SAVE or COMPLETE\", \"digitalDocumentType\" : \"DIGITAL_PROXY or JOINT_OWNERSHIP_DOCUMENT or ETC_DOCUMENT\", \"joinUserCount\" : \"전자문서 참여자수\", \"shareholdingRatio\" : \"지분율\", \"targetStartDate\" : \"전자문서 시작일자\", \"targetEndDate\" : \"전자문서 종료일자\", \"user\" : \"전자문서 위임인 정보\", \"stock\" : \"전자문서 위임인 주식정보\", \"acceptUser\" : \"전자문서 수임인 정보\", \"items\" : \"전자문서 위임장 찬성/반대/기권 기본 선택정보\", \"attachOptions\" : \"전자문서 첨부 옵션\", \"digitalDocumentDownload\" : \"전자문서 다운로드 정보\" }, \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"id\" : 0, \"title\" : \"title\", \"stock\" : { \"code\" : \"종목코드\", \"name\" : \"종목명\", \"totalIssuedQuantity\" : \"상장주식수\", \"representativePhoneNumber\" : \"(02) 555-6666\", \"memberCount\" : \"주주수\", \"stake\" : \"지분율\" }, \"content\" : \"content\", \"digitalDocument\" : { \"id\" : \"digitalDocumentId\", \"answerStatus\" : \"SAVE or COMPLETE\", \"digitalDocumentType\" : \"DIGITAL_PROXY or JOINT_OWNERSHIP_DOCUMENT or ETC_DOCUMENT\", \"joinUserCount\" : \"전자문서 참여자수\", \"shareholdingRatio\" : \"지분율\", \"targetStartDate\" : \"전자문서 시작일자\", \"targetEndDate\" : \"전자문서 종료일자\", \"user\" : \"전자문서 위임인 정보\", \"stock\" : \"전자문서 위임인 주식정보\", \"acceptUser\" : \"전자문서 수임인 정보\", \"items\" : \"전자문서 위임장 찬성/반대/기권 기본 선택정보\", \"attachOptions\" : \"전자문서 첨부 옵션\", \"digitalDocumentDownload\" : \"전자문서 다운로드 정보\" }, \"updatedAt\" : \"2000-01-23T04:56:07.000+00:00\" } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
