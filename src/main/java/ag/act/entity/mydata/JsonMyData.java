package ag.act.entity.mydata;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
public class JsonMyData {

    private List<JsonMyDataStock> jsonMyDataStockList = new ArrayList<>();

    @JsonIgnore
    public Map<String, List<JsonMyDataStock>> getJsonMyDataStocksMapByMyDataProdCode() {
        return getJsonMyDataStockList()
            .stream()
            .collect(Collectors.groupingBy(JsonMyDataStock::getMyDataProdCode));
    }

    @JsonIgnore
    public Map<String, List<JsonMyDataStock>> getJsonMyDataStocksMapByStockCode() {
        return getJsonMyDataStockList()
            .stream()
            .collect(Collectors.groupingBy(JsonMyDataStock::getCode));
    }

    @JsonIgnore
    public Optional<JsonMyDataStock> getJsonMyDataStockByStockCode(String stockCode, LocalDate referenceDate) {
        return getJsonMyDataStocksMapByStockCode().getOrDefault(stockCode, new ArrayList<>())
            .stream()
            .filter(element -> referenceDate.equals(element.getReferenceDate()))
            .findFirst();
    }
}
