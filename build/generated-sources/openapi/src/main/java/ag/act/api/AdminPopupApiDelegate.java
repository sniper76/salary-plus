package ag.act.api;

import ag.act.model.GetPopupDataResponse;
import ag.act.model.PopupDataResponse;
import ag.act.model.PopupDetailsDataResponse;
import ag.act.model.PopupRequest;
import ag.act.model.SimpleStringResponse;
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
 * A delegate to be called by the {@link AdminPopupApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminPopupApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/popups : 팝업 생성하기
     *
     * @param popupRequest  (required)
     * @return Successful response (status code 200)
     * @see AdminPopupApi#createPopup
     */
    default ResponseEntity<PopupDataResponse> createPopup(PopupRequest popupRequest) {
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

    /**
     * DELETE /api/admin/popups/{popupId} : 팝업 삭제하기
     *
     * @param popupId Popup Id (required)
     * @return Successful (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see AdminPopupApi#deletePopup
     */
    default ResponseEntity<SimpleStringResponse> deletePopup(Long popupId) {
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
     * GET /api/admin/popups/{popupId} : 팝업 정보 상세조회
     *
     * @param popupId Popup ID (required)
     * @return Successful (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see AdminPopupApi#getPopupDetailsAdmin
     */
    default ResponseEntity<PopupDetailsDataResponse> getPopupDetailsAdmin(Long popupId) {
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

    /**
     * GET /api/admin/popups : 팝업 목록 조회하기
     *
     * @param popupStatus 예약중, 게시중, 종료 구분 (READY / PROCESSING / COMPLETE) (optional)
     * @param searchType SearchType (TITLE) (optional)
     * @param searchKeyword Search keyword (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     * @see AdminPopupApi#getPopupsAdmin
     */
    default ResponseEntity<GetPopupDataResponse> getPopupsAdmin(String popupStatus,
        String searchType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"id\" : 1, \"title\" : \"팝업 제목\", \"content\" : \"팝업 내용\", \"targetStartDatetime\" : \"2023-08-10T08:22:44.548Z\", \"targetEndDatetime\" : \"2023-08-10T08:22:44.548Z\", \"popupStatus\" : \"READY / PROCESSING / COMPLETE\", \"linkType\" : \"LINK / NOTIFICATION / MAIN_HOME / NEWS_HOME / STOCK_HOME / NONE\", \"linkTitle\" : \"링크 타이틀\", \"linkUrl\" : \"/stock/000990/board/active/post/1 or /globalboard or null\", \"displayTargetType\" : \"MAIN_HOME or NEWS_HOME or STOCK_HOME\", \"stockCode\" : \"145020\", \"stockName\" : \"종목명\", \"stockGroupId\" : 14, \"stockGroupName\" : \"종목 그룹명\", \"stockTargetType\" : \"ALL / STOCK / STOCK_GROUP\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\", \"postId\" : \"Post ID\" }, { \"id\" : 1, \"title\" : \"팝업 제목\", \"content\" : \"팝업 내용\", \"targetStartDatetime\" : \"2023-08-10T08:22:44.548Z\", \"targetEndDatetime\" : \"2023-08-10T08:22:44.548Z\", \"popupStatus\" : \"READY / PROCESSING / COMPLETE\", \"linkType\" : \"LINK / NOTIFICATION / MAIN_HOME / NEWS_HOME / STOCK_HOME / NONE\", \"linkTitle\" : \"링크 타이틀\", \"linkUrl\" : \"/stock/000990/board/active/post/1 or /globalboard or null\", \"displayTargetType\" : \"MAIN_HOME or NEWS_HOME or STOCK_HOME\", \"stockCode\" : \"145020\", \"stockName\" : \"종목명\", \"stockGroupId\" : 14, \"stockGroupName\" : \"종목 그룹명\", \"stockTargetType\" : \"ALL / STOCK / STOCK_GROUP\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\", \"postId\" : \"Post ID\" } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /api/admin/popups/{popupId} : 팝업 수정하기
     *
     * @param popupId Popup Id (required)
     * @param popupRequest  (required)
     * @return Successful response (status code 200)
     * @see AdminPopupApi#updatePopup
     */
    default ResponseEntity<PopupDataResponse> updatePopup(Long popupId,
        PopupRequest popupRequest) {
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
