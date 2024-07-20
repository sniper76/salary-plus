package ag.act.dto;

import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.virtualboard.VirtualBoardCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetBoardGroupPostDto {
    private static final String SEARCH_ALL_CATEGORIES = "ALL";
    private String stockCode;
    private String boardGroupName;
    private Boolean isExclusiveToHolders;

    @Builder.Default
    private Boolean isExclusiveToPublic = false;
    @Builder.Default
    public Boolean isNotDeleted = false;

    private List<String> boardCategories;

    public BoardGroup getBoardGroup() {
        return BoardGroup.fromValue(boardGroupName);
    }

    public List<BoardCategory> getBoardCategories() {
        return emptyIfNull(boardCategories)
            .stream()
            .map(BoardCategory::fromValue)
            .filter(Objects::nonNull)
            .toList();
    }

    public String getFirstBoardCategoryName() {
        final BoardCategory firstBoardCategory = getFirstBoardCategory();
        return Optional.ofNullable(firstBoardCategory)
            .map(BoardCategory::name)
            .orElse(null);
    }

    public BoardCategory getFirstBoardCategory() {
        final List<BoardCategory> boardCategories1 = getBoardCategories();
        if (CollectionUtils.isEmpty(boardCategories1)) {
            return null;
        }

        return boardCategories1.get(0);
    }

    public boolean isExclusiveToHolders() {
        return Objects.equals(isExclusiveToHolders, Boolean.TRUE)
            && isActBestStockBoardCategory();
    }

    private boolean isActBestStockBoardCategory() {
        if (CollectionUtils.isEmpty(boardCategories)) {
            return false;
        }

        return VirtualBoardCategory.isActBestStockBoardCategory(boardCategories.get(0)); // TODO 0 ???
    }

    public boolean isValidBoardCategory() {
        final String firstBoardCategoryName = getFirstBoardCategoryName();

        return StringUtils.isNotEmpty(firstBoardCategoryName)
            && !SEARCH_ALL_CATEGORIES.equalsIgnoreCase(firstBoardCategoryName);
    }

    public List<String> getBoardCategoryNames() {
        return boardCategories;
    }
}
