package ag.act.service.download.csv.record;

import ag.act.entity.digitaldocument.DigitalDocumentItemUserAnswer;
import ag.act.enums.DigitalAnswerType;
import ag.act.service.download.csv.dto.DigitalDocumentCsvRecordInputDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class DigitalDocumentCsvRecordGenerator {

    protected static final List<String> DEFAULT_HEADERS = List.of(
        "번호", "이름", "생년월일", "성별", "주소", "상세주소", "우편번호", "전화번호", "주식명", "주식수", "평균매수가", "차입금", "작성일시"
    );

    protected abstract boolean isSupport();

    protected abstract List<String> toDefaultCsvRecord(DigitalDocumentCsvRecordInputDto digitalDocumentCsvRecordInputDto);

    public List<String> getHeaders() {
        return DEFAULT_HEADERS;
    }

    public String[] toCsvRecord(DigitalDocumentCsvRecordInputDto digitalDocumentCsvRecordInputDto) {
        final List<String> userAnswers = toCsvRecordFromUserAnswers(digitalDocumentCsvRecordInputDto.getDigitalDocumentItemUserAnswerList());
        final List<String> defaultValues = toDefaultCsvRecord(digitalDocumentCsvRecordInputDto);

        return concatenateCsvRecords(defaultValues, userAnswers);
    }

    private List<String> toCsvRecordFromUserAnswers(List<DigitalDocumentItemUserAnswer> userAnswerDtoList) {
        return Optional.ofNullable(userAnswerDtoList)
            .map(digitalDocumentItemUserAnswers -> digitalDocumentItemUserAnswers.stream()
                .map(DigitalDocumentItemUserAnswer::getAnswerSelectValue)
                .map(DigitalAnswerType::getDisplayName)
                .toList()
            ).orElse(List.of());
    }

    private String[] concatenateCsvRecords(List<String> defaultValues, List<String> userAnswers) {
        return Stream.concat(defaultValues.stream(), userAnswers.stream())
            .toArray(String[]::new);
    }
}
