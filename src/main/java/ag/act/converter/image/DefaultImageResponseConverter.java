package ag.act.converter.image;

import ag.act.converter.DateTimeConverter;
import ag.act.core.infra.S3Environment;
import ag.act.entity.FileContent;
import ag.act.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultImageResponseConverter {

    private final S3Environment s3Environment;

    @Autowired
    public DefaultImageResponseConverter(S3Environment s3Environment) {
        this.s3Environment = s3Environment;
    }

    public ag.act.model.DefaultProfileImageResponse convert(FileContent fileContent) {
        return new ag.act.model.DefaultProfileImageResponse()
            .id(fileContent.getId())
            .url(convertImageUrl(fileContent))
            .gender(fileContent.getDescription())
            .createdAt(DateTimeConverter.convert(fileContent.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(fileContent.getUpdatedAt()));
    }

    public ag.act.model.DefaultProfileImagesResponse convert(List<ag.act.model.DefaultProfileImageResponse> list) {
        return new ag.act.model.DefaultProfileImagesResponse().data(list);
    }

    public String convertImageUrl(FileContent fileContent) {
        return ImageUtil.getFullPath(s3Environment.getBaseUrl(), fileContent.getFilename());
    }

}
