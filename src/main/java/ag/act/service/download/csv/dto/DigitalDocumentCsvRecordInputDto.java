package ag.act.service.download.csv.dto;

import ag.act.dto.SimpleUserDto;
import ag.act.entity.digitaldocument.DigitalDocumentItemUserAnswer;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@EqualsAndHashCode
@RequiredArgsConstructor
public class DigitalDocumentCsvRecordInputDto {

    @Getter
    private final DigitalDocumentUser digitalDocumentUser;
    @Getter
    private final List<DigitalDocumentItemUserAnswer> digitalDocumentItemUserAnswerList;
    private final Supplier<List<SimpleUserDto>> simpleUserSupplier;
    private Map<Long, SimpleUserDto> simpleUserDtoMap;

    public Map<Long, SimpleUserDto> getSimpleUserDtoMap() {
        initializeSimpleUserDtoMap();

        return simpleUserDtoMap;
    }

    private void initializeSimpleUserDtoMap() {
        if (simpleUserDtoMap != null) {
            return;
        }

        simpleUserDtoMap = Optional.ofNullable(simpleUserSupplier)
            .map(Supplier::get)
            .orElse(List.of())
            .stream()
            .collect(Collectors.toMap(SimpleUserDto::getId, Function.identity()));
    }
}
