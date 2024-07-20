package ag.act.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"pageable", "number", "numberOfElements", "first", "last", "empty"})
public class SimplePageDto<T> extends PageImpl<T> {

    public SimplePageDto(Page<T> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    @JsonProperty("page")
    public int getPage() {
        return super.getNumber();
    }

    @JsonProperty("size")
    public int getSize() {
        return super.getSize();
    }

    @JsonProperty("sorts")
    public List<String> getSorts() {
        return super.getSort()
            .stream()
            .map(order -> order.getProperty() + "," + order.getDirection().name())
            .collect(Collectors.toList());
    }
}