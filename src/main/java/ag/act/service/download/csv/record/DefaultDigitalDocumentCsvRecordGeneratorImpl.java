package ag.act.service.download.csv.record;

import ag.act.service.download.csv.dto.DigitalDocumentCsvRecordInputDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DefaultDigitalDocumentCsvRecordGeneratorImpl extends DigitalDocumentCsvRecordGenerator {

    @Override
    protected boolean isSupport() {
        return false;
    }

    public List<String> toDefaultCsvRecord(DigitalDocumentCsvRecordInputDto digitalDocumentCsvRecordInputDto) {
        log.error(
            "Not found digital download csv recorder generator for the DigitalDocumentUser: {}",
            digitalDocumentCsvRecordInputDto.getDigitalDocumentUser()
        );
        return List.of();
    }
}
