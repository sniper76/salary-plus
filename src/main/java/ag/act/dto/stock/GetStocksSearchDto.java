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
public class GetStocksSearchDto {
    private String code;
    @Getter
    private PageRequest pageRequest;
    @Getter
    private Boolean isOnlyPrivateStocks;

    public String getCode() {
        return Optional.ofNullable(code).map(String::trim).orElse(null);
    }

    public List<String> getCodes() {
        return Optional.ofNullable(getCode()).map(List::of).orElse(List.of());
    }
}
