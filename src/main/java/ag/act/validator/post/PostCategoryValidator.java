package ag.act.validator.post;

import ag.act.core.holder.RequestContextHolder;
import ag.act.enums.BoardCategory;
import ag.act.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostCategoryValidator {

    public void validateForDelete(BoardCategory boardCategory) {
        if (!boardCategory.isDeletable()) {
            throw new BadRequestException("%s 카테고리는 삭제할 수 없습니다.".formatted(boardCategory.getDisplayName()));
        }
    }

    public void validateForUpdate(BoardCategory boardCategory) {
        if (!boardCategory.isEditable()) {
            throw new BadRequestException("%s 카테고리는 수정할 수 없습니다.".formatted(boardCategory.getDisplayName()));
        }
    }

    public void validateForCreate(String boardCategoryName) {
        final Optional<BoardCategory> optionalBoardCategory = BoardCategory.findBoardCategory(boardCategoryName);
        optionalBoardCategory
            .ifPresentOrElse(it -> {
                if (!it.isCreatable()) {
                    throw new BadRequestException("%s 카테고리는 등록할 수 없습니다.".formatted(it.getDisplayName()));
                }
                if (!it.isCreatableFromCms() && RequestContextHolder.isCmsApi()) {
                    throw new BadRequestException("%s 카테고리는 CMS에서 등록할 수 없습니다.".formatted(it.getDisplayName()));
                }
            }, () -> {
                throw new BadRequestException("게시글 카테고리를 확인해 주세요.");
            });
    }
}
