package ag.act.api;

import ag.act.model.PopupDetailsDataResponse;
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
 * A delegate to be called by the {@link PopupApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface PopupApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/popup/exclusive : 여러개의 팝업중에 사용자에게 가장 중요한 팝업을 선별하여 회신한다
     *
     * @param displayTargetType MAIN_HOME or NEWS_HOME or STOCK_HOME (required)
     * @param stockCode Stock code (optional)
     * @return Successful (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see PopupApi#getExclusivePopup
     */
    default ResponseEntity<PopupDetailsDataResponse> getExclusivePopup(String displayTargetType,
        String stockCode) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : 1, \"title\" : \"팝업 제목\", \"content\" : \"팝업 내용\", \"targetStartDatetime\" : \"2023-08-10T08:22:44.548Z\", \"targetEndDatetime\" : \"2023-08-10T08:22:44.548Z\", \"popupStatus\" : \"READY / PROCESSING / COMPLETE\", \"linkType\" : \"LINK / NOTIFICATION / MAIN_HOME / NEWS_HOME / STOCK_HOME / NONE\", \"linkTitle\" : \"링크 타이틀\", \"linkUrl\" : \"/stock/000990/board/active/post/1 or /globalboard or null\", \"displayTargetType\" : \"MAIN_HOME or NEWS_HOME or STOCK_HOME\", \"stockCode\" : \"145020\", \"stockName\" : \"종목명\", \"stockGroupId\" : 14, \"stockGroupName\" : \"종목 그룹명\", \"stockTargetType\" : \"ALL / STOCK / STOCK_GROUP\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\", \"postId\" : \"Post ID\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
