package ag.act.api;

import ag.act.model.BoardCategoryDataArrayResponse;
import ag.act.model.ErrorResponse;
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
 * A delegate to be called by the {@link StockBoardGroupCategoryApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface StockBoardGroupCategoryApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/stocks/{stockCode}/board-groups/{boardGroupName}/categories : 종목별 게시판 그룹별 카테고리 조회
     *
     * @param stockCode Stock code parameter (required)
     * @param boardGroupName Board group name parameter (required)
     * @return Successful response (status code 200)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     * @see StockBoardGroupCategoryApi#getBoardGroupCategories
     */
    default ResponseEntity<BoardCategoryDataArrayResponse> getBoardGroupCategories(String stockCode,
        String boardGroupName) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : [ { \"name\" : \"DAILY_ACT\", \"displayName\" : \"분석자료\", \"isExclusiveToHolders\" : \"ACT BEST 카테고리 주주글만 보기 여부\", \"badgeColor\" : \"#FFFFFF\", \"isGroup\" : \"false\", \"subCategories\" : \"하위 카테고리 목록\" }, { \"name\" : \"DAILY_ACT\", \"displayName\" : \"분석자료\", \"isExclusiveToHolders\" : \"ACT BEST 카테고리 주주글만 보기 여부\", \"badgeColor\" : \"#FFFFFF\", \"isGroup\" : \"false\", \"subCategories\" : \"하위 카테고리 목록\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
