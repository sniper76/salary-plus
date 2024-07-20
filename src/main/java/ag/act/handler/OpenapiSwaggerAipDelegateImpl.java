package ag.act.handler;

import ag.act.api.OpenapiSwaggerApiDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OpenapiSwaggerAipDelegateImpl implements OpenapiSwaggerApiDelegate {

    private final Resource resourceFile;

    public OpenapiSwaggerAipDelegateImpl(
        @Value("classpath:/static/openapi-bundled.yaml")
        Resource resourceFile
    ) {
        this.resourceFile = resourceFile;
    }

    @Override
    public ResponseEntity<Resource> openapiYaml() {
        return ResponseEntity.ok(resourceFile);
    }
}
