package ag.act.dto;

import ag.act.enums.DigitalDocumentType;
import ag.act.enums.admin.PostSearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class GetPostDigitalDocumentSearchDto {
    private String searchType;
    private String searchKeyword;
    private Long acceptorId;
    private DigitalDocumentType digitalDocumentType;
    private PageRequest pageRequest;

    public String getSearchKeyword() {
        if (StringUtils.isBlank(searchKeyword)) {
            return "";
        }

        return searchKeyword.trim();
    }

    public PostSearchType getSearchType() {
        return PostSearchType.fromValue(searchType);
    }

    public boolean isBlankSearchKeyword() {
        return StringUtils.isBlank(searchKeyword);
    }
}
