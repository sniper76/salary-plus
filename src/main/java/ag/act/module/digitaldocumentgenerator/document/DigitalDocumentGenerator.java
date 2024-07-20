package ag.act.module.digitaldocumentgenerator.document;

import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.module.digitaldocumentgenerator.dto.GenerateDigitalDocumentDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@SuppressWarnings("AbbreviationAsWordInName")
public class DigitalDocumentGenerator {

    private final List<IDigitalDocumentGenerator> digitalDocumentGenerators;
    private final DefaultDigitalDocumentGenerator defaultDigitalDocumentGenerator;

    public PdfDataDto generate(
        GenerateDigitalDocumentDto dto,
        DigitalDocument digitalDocument,
        DigitalDocumentUser digitalDocumentUser
    ) {
        return digitalDocumentGenerators.stream()
            .filter(generator -> generator.supports(digitalDocument.getType()))
            .findFirst()
            .orElse(defaultDigitalDocumentGenerator)
            .generate(dto, digitalDocument, digitalDocumentUser);
    }
}
