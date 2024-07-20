package ag.act.dto.digitaldocument;

import ag.act.enums.admin.UserSearchType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class DigitalDocumentUserSearchDto {
    private Long digitalDocumentId;
    private UserSearchType userSearchType;
    private String keyword;
    private Pageable pageable;

    public DigitalDocumentUserSearchDto(Long digitalDocumentId, String searchTypeName, String keyword, Pageable pageable) {
        this.digitalDocumentId = digitalDocumentId;
        this.keyword = keyword;
        this.userSearchType = UserSearchType.fromValue(searchTypeName);
        this.pageable = pageable;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(keyword);
    }

    public boolean isNameSearch() {
        return !isEmpty() && UserSearchType.NAME == userSearchType;
    }

    public String getKeyword() {
        return Optional.ofNullable(keyword).map(String::trim).orElse(null);
    }

}
