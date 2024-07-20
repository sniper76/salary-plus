package ag.act.dto.stock.home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvertStockHomeSectionResponseDto {
    private String appLinkUrl;
    private String headerTitle;
    private List<ag.act.model.SectionItemResponse> listItems;
}
