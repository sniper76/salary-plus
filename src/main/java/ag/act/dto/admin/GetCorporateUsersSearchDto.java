package ag.act.dto.admin;

import ag.act.enums.admin.CorporateUserSearchType;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;

@Getter
public class GetCorporateUsersSearchDto {
    private CorporateUserSearchType searchType;
    private String searchKeyword;
    private PageRequest pageRequest;

    public GetCorporateUsersSearchDto searchType(String searchType) {
        this.searchType = CorporateUserSearchType.fromValue(searchType);
        return this;
    }

    public GetCorporateUsersSearchDto searchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
        return this;
    }

    public GetCorporateUsersSearchDto pageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
        return this;
    }

    public boolean isBlankSearchKeyword() {
        return StringUtils.isBlank(searchKeyword);
    }
}
