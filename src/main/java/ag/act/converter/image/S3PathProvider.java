package ag.act.converter.image;

import ag.act.core.infra.S3Environment;
import ag.act.entity.FileContent;
import ag.act.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3PathProvider {

    private final S3Environment s3Environment;

    public String getFullPath(FileContent fileContent) {
        return ImageUtil.getFullPath(s3Environment.getBaseUrl(), fileContent.getFilename());
    }

    public String getContentPath(String contentFullPath) {
        return contentFullPath.replace(s3Environment.getBaseUrlWithTailingSlash(), "");
    }
}
