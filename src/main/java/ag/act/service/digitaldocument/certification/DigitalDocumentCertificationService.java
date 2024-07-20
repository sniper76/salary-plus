package ag.act.service.digitaldocument.certification;

import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.facade.FileFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DigitalDocumentCertificationService {
    private final DigitalDocumentCertificationGeneratorResolver digitalDocumentCertificationGeneratorResolver;
    private final FileFacade fileFacade;

    public void generate(
        DigitalDocument digitalDocument,
        DigitalDocumentUser digitalDocumentUser
    ) {
        doGenerate(digitalDocument, digitalDocumentUser)
            .ifPresent(pdfBytes -> upload(pdfBytes, digitalDocument, digitalDocumentUser.getUserId()));
    }

    private String upload(byte[] pdfBytes, DigitalDocument digitalDocument, Long userId) {
        return fileFacade.uploadDigitalDocumentCertification(
            pdfBytes,
            digitalDocument,
            userId
        );
    }

    private Optional<byte[]> doGenerate(
        DigitalDocument digitalDocument,
        DigitalDocumentUser digitalDocumentUser
    ) {
        return digitalDocumentCertificationGeneratorResolver.resolve(digitalDocument.getType())
            .map(generator -> generator.generate(digitalDocument, digitalDocumentUser));
    }
}
