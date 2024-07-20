package ag.act.dto;

import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.admin.PostSearchType;
import ag.act.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetPostsSearchDto {
    private String searchType;
    private String searchKeyword;
    private String boardGroupName;
    private String boardCategoryName;
    private String statusName;
    @Getter
    private LocalDateTime searchStartDate;
    @Getter
    private LocalDateTime searchEndDate;
    @Getter
    private PageRequest pageRequest;

    public BoardGroup getBoardGroup() {
        return BoardGroup.fromValue(boardGroupName);
    }

    public Optional<BoardCategory> getBoardCategory() {
        return Optional.ofNullable(BoardCategory.fromValue(boardCategoryName));
    }

    public List<Status> getStatuses() {
        return getStatus()
            .map(List::of)
            .orElseGet(() -> List.of(
                Status.ACTIVE,
                Status.INACTIVE_BY_ADMIN,
                Status.DELETED_BY_ADMIN,
                Status.DELETED_BY_USER
            ));
    }

    private Optional<Status> getStatus() {
        try {
            return Optional.of(Status.fromValue(statusName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public PostSearchType getPostSearchType() {
        return PostSearchType.fromValue(searchType);
    }

    public String getSearchKeyword() {
        if (StringUtils.isBlank(searchKeyword)) {
            return "";
        }

        return searchKeyword.trim();
    }

    public boolean isSearchByStockCode() {
        return PostSearchType.STOCK_CODE == getPostSearchType()
            && StringUtils.isNotBlank(getSearchKeyword());
    }

    public List<BoardCategory> getBoardCategories() {
        return getBoardCategory()
            .map(List::of)
            .orElseGet(() -> getBoardGroup().getCategories());
    }
}
