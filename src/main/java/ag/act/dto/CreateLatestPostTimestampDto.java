package ag.act.dto;

import ag.act.entity.Stock;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class CreateLatestPostTimestampDto {
    private Stock stock;
    private BoardGroup boardGroup;
    private BoardCategory boardCategory;
}
