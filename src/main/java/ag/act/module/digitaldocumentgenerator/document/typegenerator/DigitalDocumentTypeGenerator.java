package ag.act.module.digitaldocumentgenerator.document.typegenerator;

import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DigitalDocumentTypeGenerator {
    private final List<IDigitalDocumentTypeGenerator> digitalDocumentTypeGeneratorList;
    private final JointOwnershipDocumentGenerator defaultDigitalDocumentGenerator;

    public PdfDataDto generate(IGenerateHtmlDocumentDto dto) {
        return digitalDocumentTypeGeneratorList.stream()
            .filter(generator -> generator.supports(dto.getDigitalDocument().getType()))
            .findFirst()
            .orElse(defaultDigitalDocumentGenerator)
            .generateDigitalDocumentPdf(dto);
    }
}
