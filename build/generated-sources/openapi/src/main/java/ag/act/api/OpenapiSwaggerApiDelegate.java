package ag.act.api;

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
 * A delegate to be called by the {@link OpenapiSwaggerApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface OpenapiSwaggerApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /openapi.yaml
     *
     * @return An openapi.yaml file (status code 200)
     * @see OpenapiSwaggerApi#openapiYaml
     */
    default ResponseEntity<org.springframework.core.io.Resource> openapiYaml() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
