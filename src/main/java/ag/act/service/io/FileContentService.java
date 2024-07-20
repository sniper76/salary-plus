package ag.act.service.io;

import ag.act.dto.file.UploadFilePathDto;
import ag.act.entity.FileContent;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.exception.NotFoundException;
import ag.act.model.Gender;
import ag.act.model.Status;
import ag.act.repository.FileContentRepository;
import ag.act.validator.DefaultObjectValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FileContentService {

    private final FileContentRepository fileContentRepository;
    private final DefaultObjectValidator defaultObjectValidator;

    public FileContent saveFileContent(
        UploadFilePathDto uploadFilePathDto,
        FileContentType fileContentType,
        FileType fileType,
        String description,
        Status status
    ) {
        final FileContent fileContent = new FileContent();
        fileContent.setOriginalFilename(uploadFilePathDto.getOriginalPath());
        fileContent.setFilename(uploadFilePathDto.getUploadPath());
        fileContent.setMimetype(uploadFilePathDto.getMimeType());
        fileContent.setFileContentType(fileContentType);
        fileContent.setFileType(fileType);
        fileContent.setDescription(description);
        fileContent.setStatus(status);

        return fileContentRepository.save(fileContent);
    }

    public List<FileContent> findAllByFileContentType(FileContentType fileContentType) {
        return fileContentRepository.findAllByFileContentTypeAndStatusIn(fileContentType, List.of(Status.ACTIVE));
    }

    public Optional<FileContent> findById(Long imageId) {
        return fileContentRepository.findById(imageId);
    }

    public List<FileContent> getDefaultProfileImages() {
        return findAllByFileContentType(FileContentType.DEFAULT_PROFILE)
            .stream()
            .filter(fileContent -> StringUtils.isNoneBlank(fileContent.getDescription()))
            .sorted(Comparator.comparing(FileContent::getDescription).reversed())
            .toList();
    }

    public List<FileContent> getDefaultProfileImagesByGender(Gender gender) {
        return getDefaultProfileImages()
            .stream()
            .filter(fileContent -> gender.toString().equals(fileContent.getDescription()))
            .toList();
    }

    public FileContent getPickOneDefaultProfileImage(Gender gender) {
        final List<FileContent> defaultProfileImagesForMale = getDefaultProfileImagesByGender(gender);
        defaultObjectValidator.validateNotEmpty(defaultProfileImagesForMale, "기본 프로필 이미지가 존재하지 않습니다.");

        return pickRandomOne(defaultProfileImagesForMale);
    }

    public FileContent delete(Long id) {
        final FileContent fileContent = fileContentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 파일입니다."));
        fileContent.setStatus(Status.DELETED_BY_USER);
        return fileContentRepository.save(fileContent);
    }

    private FileContent pickRandomOne(List<FileContent> fileContents) {
        List<FileContent> targetFileContents = new ArrayList<>(fileContents);
        Collections.shuffle(targetFileContents);
        return targetFileContents.get(0);
    }

    public Optional<FileContent> findByFilename(String filename) {
        return fileContentRepository.findByFilename(filename);
    }
}
