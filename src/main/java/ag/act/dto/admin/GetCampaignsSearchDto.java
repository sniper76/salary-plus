package ag.act.dto.admin;

import ag.act.enums.BoardCategory;
import ag.act.enums.admin.CampaignSearchType;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;

@Getter
public class GetCampaignsSearchDto {
    private CampaignSearchType searchType;
    private String searchKeyword;
    private BoardCategory boardCategory;
    private PageRequest pageRequest;

    public GetCampaignsSearchDto boardCategory(String boardCategory) {
        if ("ALL".equals(boardCategory)) {
            return this;
        }
        this.boardCategory = BoardCategory.fromValue(boardCategory);
        return this;
    }

    public GetCampaignsSearchDto searchType(String searchType) {
        this.searchType = CampaignSearchType.fromValue(searchType);
        return this;
    }

    public GetCampaignsSearchDto searchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
        return this;
    }

    public GetCampaignsSearchDto pageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
        return this;
    }

    public boolean isBlankSearchKeyword() {
        return StringUtils.isBlank(searchKeyword);
    }

    public String getNullableBoardCategoryName() {
        return boardCategory == null ? null : boardCategory.name();
    }
}
