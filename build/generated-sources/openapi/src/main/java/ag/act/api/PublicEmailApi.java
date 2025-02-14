/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.SenderEmailRequest;
import ag.act.model.SimpleStringResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
@Validated
public interface PublicEmailApi {

    default PublicEmailApiDelegate getDelegate() {
        return new PublicEmailApiDelegate() {};
    }

    /**
     * POST /public-api/email
     *
     * @param xApiKey Public API Key (required)
     * @param senderEmailRequest  (required)
     * @return Success (status code 200)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/public-api/email",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> sendEmailInPublic(
        @NotNull  @RequestHeader(value = "x-api-key", required = true) String xApiKey,
         @Valid @RequestBody SenderEmailRequest senderEmailRequest
    ) {
        return getDelegate().sendEmailInPublic(xApiKey, senderEmailRequest);
    }

}
