package ag.act.converter.image;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.FileContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageResponseConverter {

    private final S3PathProvider s3PathProvider;

    public ag.act.model.SimpleImageDataResponse convert(ag.act.model.SimpleImageResponse simpleImageResponse) {
        return new ag.act.model.SimpleImageDataResponse().data(simpleImageResponse);
    }

    public ag.act.model.ImageUploadResponse convert(ag.act.model.FileContentResponse fileContentResponse) {
        return new ag.act.model.ImageUploadResponse().data(fileContentResponse);
    }

    public ag.act.model.FileContentResponse convert(FileContent fileContent) {
        return new ag.act.model.FileContentResponse()
            .id(fileContent.getId())
            .url(convertImageUrl(fileContent))
            .originalFilename(fileContent.getOriginalFilename())
            .description(fileContent.getDescription())
            .fileContentType(fileContent.getFileContentType().toString())
            .fileType(fileContent.getFileType().name())
            .createdAt(DateTimeConverter.convert(fileContent.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(fileContent.getUpdatedAt()))
            .deletedAt(DateTimeConverter.convert(fileContent.getDeletedAt()));
    }

    public ag.act.model.SimpleImageResponse convertToSimpleImageResponse(FileContent fileContent) {
        return new ag.act.model.SimpleImageResponse()
            .imageId(fileContent.getId())
            .imageUrl(convertImageUrl(fileContent));
    }

    public String convertImageUrl(FileContent fileContent) {
        return s3PathProvider.getFullPath(fileContent);
    }
}
