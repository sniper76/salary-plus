package ag.act.module.digitaldocumentgenerator.document;

import ag.act.dto.file.FilenameDto;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentFilenameGenerator;
import ag.act.util.FilenameUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HolderListReadAndCopyDigitalDocumentFilenameProvider {
    private final DigitalDocumentFilenameGenerator digitalDocumentFilenameGenerator;

    public FilenameDto getFilenameDto(DigitalDocument digitalDocument, String suffix) {
        final String originalFilename = digitalDocumentFilenameGenerator.generateHolderListReadAndCopyFilename(
            digitalDocument, suffix
        );

        final String fullPath = FilenameUtil.getDigitalDocumentUploadPath(
            digitalDocument.getId(),
            originalFilename
        );

        final String baseName = FilenameUtils.getBaseName(fullPath);
        final String filename = FilenameUtils.getName(fullPath);

        return new FilenameDto(filename, baseName, fullPath);
    }
}
