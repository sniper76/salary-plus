package ag.act.configuration.initial;

import ag.act.entity.FileContent;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.model.Status;
import ag.act.repository.FileContentRepository;
import ag.act.service.io.FileContentService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Transactional
public class UserDefaultProfilesLoader implements InitialLoader {

    private final List<String> maleImages;
    private final List<String> femaleImages;
    private final FileContentService fileContentService;
    private final FileContentRepository fileContentRepository;

    public UserDefaultProfilesLoader(
        @Value("${act.default-user-profile-images.male}") String[] maleImages,
        @Value("${act.default-user-profile-images.female}") String[] femaleImages,
        FileContentService fileContentService,
        FileContentRepository fileContentRepository
    ) {
        this.maleImages = Arrays.asList(maleImages);
        this.femaleImages = Arrays.asList(femaleImages);
        this.fileContentService = fileContentService;
        this.fileContentRepository = fileContentRepository;
    }

    @Override
    public void load() {
        if (alreadyHasUserDefaultProfileImages()) {
            return;
        }

        maleImages.forEach(imageFilename -> this.createFileContent(imageFilename.trim(), "M"));
        femaleImages.forEach(imageFilename -> this.createFileContent(imageFilename.trim(), "F"));
    }

    private void createFileContent(String imageFilename, String description) {
        if (StringUtils.isBlank(imageFilename)) {
            return;
        }

        FileContent fileContent = new FileContent();
        fileContent.setFileType(FileType.IMAGE);
        fileContent.setFileContentType(FileContentType.DEFAULT_PROFILE);
        fileContent.setFilename(imageFilename);
        fileContent.setOriginalFilename(imageFilename);
        fileContent.setStatus(Status.ACTIVE);
        fileContent.setMimetype("image/png");
        fileContent.setDescription(description);
        fileContentRepository.save(fileContent);
    }

    private boolean alreadyHasUserDefaultProfileImages() {
        return !fileContentService.findAllByFileContentType(FileContentType.DEFAULT_PROFILE).isEmpty();
    }
}
