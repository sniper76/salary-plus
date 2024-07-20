package ag.act.service.download.csv.record;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.csv.CsvDataConverter;
import ag.act.dto.SimpleUserDto;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.service.download.csv.dto.DigitalDocumentCsvRecordInputDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminDigitalDocumentCsvRecordGeneratorImpl extends DigitalDocumentCsvRecordGenerator {
    private final CsvDataConverter csvDataConverter;

    @Override
    protected boolean isSupport() {
        return ActUserProvider.getNoneNull().isAdmin();
    }

    @Override
    public List<String> getHeaders() {
        return List.of(
            "번호", "이름", "생년월일", "성별", "주소", "상세주소", "우편번호", "전화번호", "주식명", "주식수", "평균매수가", "차입금", "작성일시", "가입일자"
        );
    }

    @Override
    public List<String> toDefaultCsvRecord(DigitalDocumentCsvRecordInputDto digitalDocumentCsvRecordInputDto) {
        final DigitalDocumentUser digitalDocumentUser = digitalDocumentCsvRecordInputDto.getDigitalDocumentUser();
        final LocalDateTime registeredAt = getRegisteredAt(digitalDocumentCsvRecordInputDto);

        return List.of(
            csvDataConverter.getStringNumber(digitalDocumentUser.getIssuedNumber()),
            digitalDocumentUser.getName(),
            csvDataConverter.getBirthday(digitalDocumentUser.getBirthDate()),
            csvDataConverter.getGender(digitalDocumentUser),
            csvDataConverter.getString(digitalDocumentUser.getAddress()),
            csvDataConverter.getString(digitalDocumentUser.getAddressDetail()),
            csvDataConverter.getString(digitalDocumentUser.getZipcode()),
            csvDataConverter.getPhoneNumber(digitalDocumentUser),
            digitalDocumentUser.getStockName(),
            csvDataConverter.getStringNumber(digitalDocumentUser.getStockCount()),
            csvDataConverter.getStringNumber(digitalDocumentUser.getPurchasePrice()),
            csvDataConverter.getStringNumber(digitalDocumentUser.getLoanPrice()),
            csvDataConverter.getUpdatedAtInKoreanTime(digitalDocumentUser),
            csvDataConverter.getRegisteredAt(registeredAt)
        );
    }

    private LocalDateTime getRegisteredAt(DigitalDocumentCsvRecordInputDto digitalDocumentCsvRecordInputDto) {
        final DigitalDocumentUser digitalDocumentUser = digitalDocumentCsvRecordInputDto.getDigitalDocumentUser();
        final SimpleUserDto simpleUserDto = digitalDocumentCsvRecordInputDto.getSimpleUserDtoMap().get(digitalDocumentUser.getUserId());
        if (simpleUserDto == null) {
            log.error("simpleUserDto is null. digitalDocumentUser: {}", digitalDocumentUser);
            return LocalDateTime.now();
        }
        return simpleUserDto.getCreatedAt();
    }
}
