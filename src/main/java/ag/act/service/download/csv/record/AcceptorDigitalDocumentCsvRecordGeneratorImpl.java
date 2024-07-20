package ag.act.service.download.csv.record;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.csv.CsvDataConverter;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.service.download.csv.dto.DigitalDocumentCsvRecordInputDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AcceptorDigitalDocumentCsvRecordGeneratorImpl extends DigitalDocumentCsvRecordGenerator {
    private final CsvDataConverter csvDataConverter;

    @Override
    protected boolean isSupport() {
        return ActUserProvider.getNoneNull().isAcceptor();
    }

    @Override
    public List<String> getHeaders() {
        return List.of("번호", "이름", "생년월일", "성별", "주식명", "주식수", "작성일시");
    }

    public List<String> toDefaultCsvRecord(DigitalDocumentCsvRecordInputDto digitalDocumentCsvRecordInputDto) {
        final DigitalDocumentUser digitalDocumentUser = digitalDocumentCsvRecordInputDto.getDigitalDocumentUser();

        return List.of(
            csvDataConverter.getStringNumber(digitalDocumentUser.getIssuedNumber()),
            digitalDocumentUser.getName(),
            csvDataConverter.getBirthday(digitalDocumentUser.getBirthDate()),
            csvDataConverter.getGender(digitalDocumentUser),
            digitalDocumentUser.getStockName(),
            csvDataConverter.getStringNumber(digitalDocumentUser.getStockCount()),
            csvDataConverter.getUpdatedAtInKoreanTime(digitalDocumentUser)
        );
    }
}
