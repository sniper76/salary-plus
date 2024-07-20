package ag.act.dto.boardcategory;

import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;

public interface IBoardCategory {

    String getDisplayName();

    String getName();

    Optional<String> getBadgeColor();

    default boolean isMultipleCategoryGroup() {
        return false;
    }

    @Nullable
    default List<IBoardCategory> getSubBoardCategories() {
        return null;
    }
}
