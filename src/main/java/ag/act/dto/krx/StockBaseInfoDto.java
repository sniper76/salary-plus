package ag.act.dto.krx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@SuppressWarnings("checkstyle:MemberName")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockBaseInfoDto {
    private List<StockItemDto> OutBlock_1;
}
