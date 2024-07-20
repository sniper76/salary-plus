package ag.act.service.download.csv.record;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DigitalDocumentCsvRecordGeneratorResolver {
    private final List<DigitalDocumentCsvRecordGenerator> digitalDocumentCsvRecordGenerators;
    private final DefaultDigitalDocumentCsvRecordGeneratorImpl defaultDigitalDocumentCsvRecordGenerator;

    public DigitalDocumentCsvRecordGenerator resolve() {
        return digitalDocumentCsvRecordGenerators
            .stream()
            .filter(DigitalDocumentCsvRecordGenerator::isSupport)
            .findFirst()
            .orElse(defaultDigitalDocumentCsvRecordGenerator);
    }
}
