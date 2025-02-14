/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package ag.act.api;

import ag.act.model.SendEmailRequest;
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
public interface DigitalDocumentEmailSenderApi {

    default DigitalDocumentEmailSenderApiDelegate getDelegate() {
        return new DigitalDocumentEmailSenderApiDelegate() {};
    }

    /**
     * POST /api/digital-document/{digitalDocumentId}/email : 전자문서 이메일 발송
     *
     * @param digitalDocumentId Digital Document ID parameter (required)
     * @param sendEmailRequest  (required)
     * @return Successful response (status code 200)
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/digital-document/{digitalDocumentId}/email",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<SimpleStringResponse> sendEmailForDigitalDocument(
         @PathVariable("digitalDocumentId") Long digitalDocumentId,
         @Valid @RequestBody SendEmailRequest sendEmailRequest
    ) {
        return getDelegate().sendEmailForDigitalDocument(digitalDocumentId, sendEmailRequest);
    }

}
