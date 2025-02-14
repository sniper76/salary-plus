package ag.act.api;

import ag.act.model.CreatePushRequest;
import ag.act.model.GetPushDataResponse;
import ag.act.model.PushDetailsDataResponse;
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
 * A delegate to be called by the {@link AdminPushApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminPushApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/pushes : 푸시 등록 생성하기
     *
     * @param createPushRequest  (required)
     * @return Successful response (status code 200)
     * @see AdminPushApi#createPushAdmin
     */
    default ResponseEntity<PushDetailsDataResponse> createPushAdmin(CreatePushRequest createPushRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : 1, \"title\" : \"푸시 제목\", \"content\" : \"푸시 내용\", \"linkUrl\" : \"/stock/000990/board/action/post/1\", \"linkType\" : \"LINK or NOTIFICATION or MAIN_HOME or NEWS_HOME or STOCK_HOME or NONE\", \"stockTargetType\" : \"ALL or STOCK or STOCK_GROUP\", \"sendType\" : \"SCHEDULE or IMMEDIATELY\", \"sentStatus\" : \"READY or PROCESSING or COMPLETE or FAIL\", \"stockCode\" : \"145020\", \"stockName\" : \"에코프로\", \"stockGroupId\" : 14, \"stockGroupName\" : \"에코프로 종목그룹\", \"postId\" : \"1 or null\", \"result\" : \"발송결과 메시지 OR 에러 메시지\", \"targetDatetime\" : \"2023-08-10T08:22:44.548Z\", \"sentStartDatetime\" : \"2023-08-10T08:22:44.548Z\", \"sentEmdDatetime\" : \"2023-08-10T08:22:44.548Z\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\", \"user\" : { \"name\" : \"이름\", \"nickname\" : \"닉네임\" } } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /api/admin/pushes/{pushId} : 푸시 예약 삭제하기
     *
     * @param pushId Push ID (required)
     * @return Successful (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see AdminPushApi#deletePushAdmin
     */
    default ResponseEntity<SimpleStringResponse> deletePushAdmin(Long pushId) {
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
     * GET /api/admin/automated-pushes : 개인 자동 푸시 목록 조회하기
     *
     * @param searchType SearchType (PUSH_TITLE / AUTHOR_NAME / AUTHOR_NICKNAME / PUSH_CONTENT) (optional)
     * @param searchKeyword Search keyword (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     * @see AdminPushApi#getAutomatedPushesAdmin
     */
    default ResponseEntity<GetPushDataResponse> getAutomatedPushesAdmin(String searchType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"id\" : 1, \"title\" : \"푸시 제목\", \"content\" : \"푸시 내용\", \"linkUrl\" : \"/stock/000990/board/action/post/1\", \"linkType\" : \"LINK or NOTIFICATION or MAIN_HOME or NEWS_HOME or STOCK_HOME or NONE\", \"stockTargetType\" : \"ALL or STOCK or STOCK_GROUP\", \"sendType\" : \"SCHEDULE or IMMEDIATELY\", \"sentStatus\" : \"READY or PROCESSING or COMPLETE or FAIL\", \"stockCode\" : \"145020\", \"stockName\" : \"에코프로\", \"stockGroupId\" : 14, \"stockGroupName\" : \"에코프로 종목그룹\", \"postId\" : \"1 or null\", \"result\" : \"발송결과 메시지 OR 에러 메시지\", \"targetDatetime\" : \"2023-08-10T08:22:44.548Z\", \"sentStartDatetime\" : \"2023-08-10T08:22:44.548Z\", \"sentEmdDatetime\" : \"2023-08-10T08:22:44.548Z\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\", \"user\" : { \"name\" : \"이름\", \"nickname\" : \"닉네임\" } }, { \"id\" : 1, \"title\" : \"푸시 제목\", \"content\" : \"푸시 내용\", \"linkUrl\" : \"/stock/000990/board/action/post/1\", \"linkType\" : \"LINK or NOTIFICATION or MAIN_HOME or NEWS_HOME or STOCK_HOME or NONE\", \"stockTargetType\" : \"ALL or STOCK or STOCK_GROUP\", \"sendType\" : \"SCHEDULE or IMMEDIATELY\", \"sentStatus\" : \"READY or PROCESSING or COMPLETE or FAIL\", \"stockCode\" : \"145020\", \"stockName\" : \"에코프로\", \"stockGroupId\" : 14, \"stockGroupName\" : \"에코프로 종목그룹\", \"postId\" : \"1 or null\", \"result\" : \"발송결과 메시지 OR 에러 메시지\", \"targetDatetime\" : \"2023-08-10T08:22:44.548Z\", \"sentStartDatetime\" : \"2023-08-10T08:22:44.548Z\", \"sentEmdDatetime\" : \"2023-08-10T08:22:44.548Z\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\", \"user\" : { \"name\" : \"이름\", \"nickname\" : \"닉네임\" } } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/pushes/{pushId} : 푸시 정보 상세조회
     *
     * @param pushId Push ID (required)
     * @return Successful (status code 200)
     *         or Bad Request (status code 400)
     *         or Not Found (status code 404)
     * @see AdminPushApi#getPushDetailsAdmin
     */
    default ResponseEntity<PushDetailsDataResponse> getPushDetailsAdmin(Long pushId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"id\" : 1, \"title\" : \"푸시 제목\", \"content\" : \"푸시 내용\", \"linkUrl\" : \"/stock/000990/board/action/post/1\", \"linkType\" : \"LINK or NOTIFICATION or MAIN_HOME or NEWS_HOME or STOCK_HOME or NONE\", \"stockTargetType\" : \"ALL or STOCK or STOCK_GROUP\", \"sendType\" : \"SCHEDULE or IMMEDIATELY\", \"sentStatus\" : \"READY or PROCESSING or COMPLETE or FAIL\", \"stockCode\" : \"145020\", \"stockName\" : \"에코프로\", \"stockGroupId\" : 14, \"stockGroupName\" : \"에코프로 종목그룹\", \"postId\" : \"1 or null\", \"result\" : \"발송결과 메시지 OR 에러 메시지\", \"targetDatetime\" : \"2023-08-10T08:22:44.548Z\", \"sentStartDatetime\" : \"2023-08-10T08:22:44.548Z\", \"sentEmdDatetime\" : \"2023-08-10T08:22:44.548Z\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\", \"user\" : { \"name\" : \"이름\", \"nickname\" : \"닉네임\" } } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /api/admin/pushes : 푸시 목록 조회하기
     *
     * @param searchType SearchType (PUSH_TITLE / STOCK_NAME / PUSH_CONTENT) (optional)
     * @param searchKeyword Search keyword (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     * @see AdminPushApi#getPushesAdmin
     */
    default ResponseEntity<GetPushDataResponse> getPushesAdmin(String searchType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"id\" : 1, \"title\" : \"푸시 제목\", \"content\" : \"푸시 내용\", \"linkUrl\" : \"/stock/000990/board/action/post/1\", \"linkType\" : \"LINK or NOTIFICATION or MAIN_HOME or NEWS_HOME or STOCK_HOME or NONE\", \"stockTargetType\" : \"ALL or STOCK or STOCK_GROUP\", \"sendType\" : \"SCHEDULE or IMMEDIATELY\", \"sentStatus\" : \"READY or PROCESSING or COMPLETE or FAIL\", \"stockCode\" : \"145020\", \"stockName\" : \"에코프로\", \"stockGroupId\" : 14, \"stockGroupName\" : \"에코프로 종목그룹\", \"postId\" : \"1 or null\", \"result\" : \"발송결과 메시지 OR 에러 메시지\", \"targetDatetime\" : \"2023-08-10T08:22:44.548Z\", \"sentStartDatetime\" : \"2023-08-10T08:22:44.548Z\", \"sentEmdDatetime\" : \"2023-08-10T08:22:44.548Z\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\", \"user\" : { \"name\" : \"이름\", \"nickname\" : \"닉네임\" } }, { \"id\" : 1, \"title\" : \"푸시 제목\", \"content\" : \"푸시 내용\", \"linkUrl\" : \"/stock/000990/board/action/post/1\", \"linkType\" : \"LINK or NOTIFICATION or MAIN_HOME or NEWS_HOME or STOCK_HOME or NONE\", \"stockTargetType\" : \"ALL or STOCK or STOCK_GROUP\", \"sendType\" : \"SCHEDULE or IMMEDIATELY\", \"sentStatus\" : \"READY or PROCESSING or COMPLETE or FAIL\", \"stockCode\" : \"145020\", \"stockName\" : \"에코프로\", \"stockGroupId\" : 14, \"stockGroupName\" : \"에코프로 종목그룹\", \"postId\" : \"1 or null\", \"result\" : \"발송결과 메시지 OR 에러 메시지\", \"targetDatetime\" : \"2023-08-10T08:22:44.548Z\", \"sentStartDatetime\" : \"2023-08-10T08:22:44.548Z\", \"sentEmdDatetime\" : \"2023-08-10T08:22:44.548Z\", \"createdAt\" : \"2023-08-10T08:22:44.548Z\", \"updatedAt\" : \"2023-08-10T08:22:44.548Z\", \"user\" : { \"name\" : \"이름\", \"nickname\" : \"닉네임\" } } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
