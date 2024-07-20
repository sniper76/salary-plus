package ag.act.service.digitaldocument;

import ag.act.core.infra.S3Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DigitalDocumentTemplatePathService {
    private final String digitalDocumentRelativePath;
    private final S3Environment s3Environment;

    public DigitalDocumentTemplatePathService(
        @Value("${act.free-marker-templates.digital-document.relative-path}") String digitalDocumentRelativePath,
        S3Environment s3Environment
    ) {
        this.digitalDocumentRelativePath = digitalDocumentRelativePath;
        this.s3Environment = s3Environment;
    }

    public String getPath() {
        return s3Environment.getBaseUrl() + '/' + digitalDocumentRelativePath + '/';
    }
}
