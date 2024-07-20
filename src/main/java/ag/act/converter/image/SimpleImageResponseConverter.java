package ag.act.converter.image;

import ag.act.entity.FileContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SimpleImageResponseConverter {

    private final S3PathProvider s3PathProvider;

    public ag.act.model.SimpleImageResponse convert(FileContent fileContent) {
        final ag.act.model.SimpleImageResponse simpleImageResponse = new ag.act.model.SimpleImageResponse();
        simpleImageResponse.setImageId(fileContent.getId());
        simpleImageResponse.setImageUrl(s3PathProvider.getFullPath(fileContent));

        return simpleImageResponse;
    }

    public List<ag.act.model.SimpleImageResponse> convert(List<FileContent> fileContents) {
        return fileContents.stream().map(this::convert).toList();
    }
}
