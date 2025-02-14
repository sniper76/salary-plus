package ag.act.api;

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
 * A delegate to be called by the {@link ShopApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface ShopApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/admin/shops : 상점과 상점 그룹 상점 근무자들을 등록한다.
     *
     * @param shopName  (optional)
     * @param pricePlanId  (optional)
     * @param userShiftStartTime  (optional)
     * @param shopGroupId  (optional)
     * @param userListFile  (optional)
     * @return Success (status code 200)
     * @see ShopApi#createShop
     */
    default ResponseEntity<SimpleStringResponse> createShop(String shopName,
        Long pricePlanId,
        String userShiftStartTime,
        Long shopGroupId,
        MultipartFile userListFile) {
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
