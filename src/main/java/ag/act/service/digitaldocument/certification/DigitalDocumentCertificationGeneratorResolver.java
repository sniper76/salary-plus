package ag.act.service.digitaldocument.certification;

import ag.act.enums.DigitalDocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class DigitalDocumentCertificationGeneratorResolver {
    private final List<DigitalDocumentCertificationGenerator> generators;

    public Optional<DigitalDocumentCertificationGenerator> resolve(DigitalDocumentType digitalDocumentType) {
        return generators.stream()
            .filter(generator -> generator.supports(digitalDocumentType))
            .findFirst();
    }
}
