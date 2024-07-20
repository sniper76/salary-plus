package ag.act.dto.boardcategory;

import ag.act.enums.BoardCategory;
import jakarta.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record BoardCategoryGroup(List<BoardCategory> boardCategories) implements IBoardCategory {

    public String getDisplayName() {
        return boardCategories
            .stream()
            .map(IBoardCategory::getDisplayName)
            .collect(Collectors.joining("/"));
    }

    public String getName() {
        return boardCategories
            .stream()
            .map(IBoardCategory::getName)
            .collect(Collectors.joining(","));
    }

    public Optional<String> getBadgeColor() {
        return Optional.ofNullable(
            isMultipleCategoryGroup()
                ? null
                : boardCategories.get(0).getBadgeColor().orElse(null)
        );
    }

    public boolean isMultipleCategoryGroup() {
        return boardCategories.size() > 1;
    }

    @Nullable
    public List<IBoardCategory> getSubBoardCategories() {
        if (isMultipleCategoryGroup()) {
            return Collections.unmodifiableList(boardCategories);
        }

        return null;
    }
}
