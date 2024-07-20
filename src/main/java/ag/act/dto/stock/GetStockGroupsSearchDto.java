package ag.act.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class GetStockGroupsSearchDto {
    private Long stockGroupId;
    private PageRequest pageRequest;

    public List<Long> getStockGroupIds() {
        return Optional.ofNullable(getStockGroupId()).map(List::of).orElse(List.of());
    }
}
