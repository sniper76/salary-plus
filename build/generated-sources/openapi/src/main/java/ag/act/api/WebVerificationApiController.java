package ag.act.api;

import ag.act.model.ErrorResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.VerificationCodeRequest;
import ag.act.model.WebVerificationCodeGenerateRequest;
import ag.act.model.WebVerificationCodeGenerateResponse;
import ag.act.model.WebVerificationCodeRequest;
import ag.act.model.WebVerificationCodeResponse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
@Controller
@RequestMapping("${openapi.salaryPlusAPIs.base-path:}")
public class WebVerificationApiController implements WebVerificationApi {

    private final WebVerificationApiDelegate delegate;

    public WebVerificationApiController(@Autowired(required = false) WebVerificationApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new WebVerificationApiDelegate() {});
    }

    @Override
    public WebVerificationApiDelegate getDelegate() {
        return delegate;
    }

}
