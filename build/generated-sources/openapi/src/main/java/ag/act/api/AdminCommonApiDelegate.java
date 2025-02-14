package ag.act.api;

import ag.act.model.CmsCommonsDataResponse;
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
 * A delegate to be called by the {@link AdminCommonApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface AdminCommonApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /api/admin/cms-commons : CMS 화면에서 필요한 기본 정보들 - simple stocks - simple stock groups 
     *
     * @return Successful response (status code 200)
     * @see AdminCommonApi#getCmsCommons
     */
    default ResponseEntity<CmsCommonsDataResponse> getCmsCommons() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : { \"boardGroups\" : [ { \"displayName\" : \"displayName\", \"name\" : \"name\", \"categories\" : [ { \"name\" : \"DAILY_ACT\", \"displayName\" : \"분석자료\", \"isExclusiveToHolders\" : \"ACT BEST 카테고리 주주글만 보기 여부\", \"badgeColor\" : \"#FFFFFF\", \"isGroup\" : \"false\", \"subCategories\" : \"하위 카테고리 목록\" }, { \"name\" : \"DAILY_ACT\", \"displayName\" : \"분석자료\", \"isExclusiveToHolders\" : \"ACT BEST 카테고리 주주글만 보기 여부\", \"badgeColor\" : \"#FFFFFF\", \"isGroup\" : \"false\", \"subCategories\" : \"하위 카테고리 목록\" } ] }, { \"displayName\" : \"displayName\", \"name\" : \"name\", \"categories\" : [ { \"name\" : \"DAILY_ACT\", \"displayName\" : \"분석자료\", \"isExclusiveToHolders\" : \"ACT BEST 카테고리 주주글만 보기 여부\", \"badgeColor\" : \"#FFFFFF\", \"isGroup\" : \"false\", \"subCategories\" : \"하위 카테고리 목록\" }, { \"name\" : \"DAILY_ACT\", \"displayName\" : \"분석자료\", \"isExclusiveToHolders\" : \"ACT BEST 카테고리 주주글만 보기 여부\", \"badgeColor\" : \"#FFFFFF\", \"isGroup\" : \"false\", \"subCategories\" : \"하위 카테고리 목록\" } ] } ], \"stockGroups\" : [ { \"name\" : \"name\", \"id\" : 0 }, { \"name\" : \"name\", \"id\" : 0 } ], \"stocks\" : [ { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" }, { \"code\" : \"code\", \"name\" : \"name\", \"standardCode\" : \"standardCode\" } ] } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
