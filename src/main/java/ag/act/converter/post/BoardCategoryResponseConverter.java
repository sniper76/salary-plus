package ag.act.converter.post;

import ag.act.dto.boardcategory.BoardCategoryGroups;
import ag.act.dto.boardcategory.IBoardCategory;
import ag.act.entity.Board;
import ag.act.model.BoardCategoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BoardCategoryResponseConverter {

    public List<BoardCategoryResponse> convert(BoardCategoryGroups boardCategoryGroups) {
        return boardCategoryGroups.boardCategoryGroups().stream()
            .map(this::convert)
            .toList();
    }

    public BoardCategoryResponse convert(IBoardCategory boardCategory) {
        return new BoardCategoryResponse()
            .name(boardCategory.getName())
            .displayName(boardCategory.getDisplayName())
            .isGroup(boardCategory.isMultipleCategoryGroup())
            .badgeColor(boardCategory.getBadgeColor().orElse(null))
            .subCategories(
                Optional.ofNullable(boardCategory.getSubBoardCategories())
                    .map(boardCategories -> boardCategories.stream().map(this::convert).toList())
                    .orElse(null)
            );
    }

    public BoardCategoryResponse convert(Board board) {
        return convert(board.getCategory());
    }
}
