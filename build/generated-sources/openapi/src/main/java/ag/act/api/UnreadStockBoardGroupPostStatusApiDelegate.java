package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.UnreadStockBoardGroupPostStatusDataResponse;
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
 * A delegate to be called by the {@link UnreadStockBoardGroupPostStatusApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface UnreadStockBoardGroupPostStatusApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/stocks/{stockCode}/unread-board-group-post-status : 종목별 게시판 그룹별 읽지 않은 게시글 상태 조회
     *
     * @param stockCode Stock code parameter (required)
     * @return Success (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see UnreadStockBoardGroupPostStatusApi#getUnreadStockBoardGroupPostStatus
     */
    default ResponseEntity<UnreadStockBoardGroupPostStatusDataResponse> getUnreadStockBoardGroupPostStatus(String stockCode) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"unreadStockBoardGroupPostStatus\" : { \"unreadDebate\" : true, \"unreadAnalysis\" : true, \"unreadAction\" : true } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
