package ag.act.api;

import ag.act.model.GetUserNotificationDataResponse;
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
 * A delegate to be called by the {@link UserNotificationApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface UserNotificationApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/notifications/{notificationId} : 사용자 알림 확인
     *
     * @param notificationId Notification ID (required)
     * @return Successful response (status code 200)
     * @see UserNotificationApi#createNotificationUserView
     */
    default ResponseEntity<SimpleStringResponse> createNotificationUserView(Long notificationId) {
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
     * GET /api/notifications : 알림 리스트 조회
     *
     * @param category Notification category parameter, (null or STOCKHOLDER_ACTION) (optional)
     * @param page Page number (optional, default to 1)
     * @param size Number of items per page (optional, default to 10)
     * @param sorts Sorting criteria (optional, default to createdAt:desc)
     * @return Successful response (status code 200)
     * @see UserNotificationApi#getUserNotifications
     */
    default ResponseEntity<GetUserNotificationDataResponse> getUserNotifications(String category,
        Integer page,
        Integer size,
        List<String> sorts) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"activeStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"post\" : { \"stockName\" : \"stockName\", \"boardCategory\" : \"boardCategory\", \"id\" : 1, \"title\" : \"title\", \"stockCode\" : \"stockCode\", \"boardCategoryDisplayName\" : \"boardCategoryDisplayName\" }, \"linkUrl\" : \"linkUrl\", \"isRead\" : true, \"id\" : 0, \"postId\" : 6, \"category\" : \"category\", \"type\" : \"type\" }, { \"createdAt\" : \"2000-01-23T04:56:07.000+00:00\", \"activeStartDate\" : \"2000-01-23T04:56:07.000+00:00\", \"post\" : { \"stockName\" : \"stockName\", \"boardCategory\" : \"boardCategory\", \"id\" : 1, \"title\" : \"title\", \"stockCode\" : \"stockCode\", \"boardCategoryDisplayName\" : \"boardCategoryDisplayName\" }, \"linkUrl\" : \"linkUrl\", \"isRead\" : true, \"id\" : 0, \"postId\" : 6, \"category\" : \"category\", \"type\" : \"type\" } ], \"paging\" : { \"size\" : 10, \"totalPages\" : 10, \"page\" : 1, \"sorts\" : \"createdAt:desc,userId:asc\", \"totalElements\" : 10 } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
