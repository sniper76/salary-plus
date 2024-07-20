package ag.act.converter.admin;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.StopWord;
import ag.act.model.StopWordResponse;
import org.springframework.stereotype.Component;

@Component
public class StopWordResponseConverter {

    public StopWordResponse convert(StopWord stopWord) {
        return new StopWordResponse()
            .id(stopWord.getId())
            .word(stopWord.getWord())
            .status(stopWord.getStatus())
            .createdAt(DateTimeConverter.convert(stopWord.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(stopWord.getUpdatedAt()));
    }
}
