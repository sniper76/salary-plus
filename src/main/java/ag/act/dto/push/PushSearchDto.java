package ag.act.dto.push;

import ag.act.enums.push.PushSearchType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class PushSearchDto {
    private PushSearchType pushSearchType;
    private String keyword;

    public PushSearchDto(String searchTypeName, String keyword) {
        this.keyword = keyword;
        this.pushSearchType = PushSearchType.fromValue(searchTypeName);
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(keyword);
    }

    public String getKeyword() {
        return Optional.ofNullable(keyword).map(String::trim).orElse(null);
    }

}
