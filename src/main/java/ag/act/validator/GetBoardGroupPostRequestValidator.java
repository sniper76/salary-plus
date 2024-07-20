package ag.act.validator;

import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.exception.BadRequestException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
public class GetBoardGroupPostRequestValidator {

    private static final int ONE_TIME = 1;

    public List<String> validateAndGetCategories(String boardCategoryName, List<String> boardCategoryNames) {
        final List<String> refinedBoardCategoryNames = getRefinedBoardCategoryNames(boardCategoryNames);

        if (StringUtils.isNotBlank(boardCategoryName) && CollectionUtils.isNotEmpty(refinedBoardCategoryNames)) {
            throw new BadRequestException("boardCategory 와 boardCategories 는 동시에 사용할 수 없습니다.");
        }

        if (CollectionUtils.isEmpty(refinedBoardCategoryNames)) {
            return getRefinedBoardCategoryNames(boardCategoryName);
        }

        final List<BoardCategory> boardCategories = getBoardCategories(refinedBoardCategoryNames);

        if (hasOnlyOneCategory(boardCategories)) {
            return refinedBoardCategoryNames;
        }

        validateCategoryCompanions(boardCategories, refinedBoardCategoryNames);

        return refinedBoardCategoryNames;
    }

    private boolean hasOnlyOneCategory(List<BoardCategory> boardCategories) {
        return boardCategories.size() == ONE_TIME;
    }

    private List<String> newArrayListWith(String boardCategoryName) {
        List<String> boardCategoryNames = new ArrayList<>();
        boardCategoryNames.add(boardCategoryName);

        return boardCategoryNames;
    }

    private void validateCategoryCompanions(List<BoardCategory> boardCategories, List<String> refinedBoardCategoryNames) {
        final boolean noneMatch = boardCategories.stream()
            .noneMatch(it -> new HashSet<>(it.getAvailableBoardCategoryCompanions()).containsAll(boardCategories));

        if (noneMatch) {
            throw new BadRequestException("%s 카테고리 조합은 조회가 불가능합니다.".formatted(refinedBoardCategoryNames));
        }
    }

    @NotNull
    private List<String> getRefinedBoardCategoryNames(String boardCategoryName) {
        return getRefinedBoardCategoryNames(newArrayListWith(boardCategoryName));
    }

    @NotNull
    private List<String> getRefinedBoardCategoryNames(List<String> boardCategoryNames) {
        return emptyIfNull(boardCategoryNames).stream()
            .filter(StringUtils::isNotBlank)
            .toList();
    }

    public void validateBoardGroupAndCategories(String boardGroupName, List<String> boardCategoryNames) {
        final Optional<BoardGroup> optionalBoardGroup = findBoardGroup(boardGroupName);
        if (optionalBoardGroup.isEmpty()) {
            return;
        }

        final BoardGroup boardGroup = optionalBoardGroup.get();
        final List<BoardCategory> boardCategories = getBoardCategories(boardCategoryNames);

        boardCategories.forEach(boardCategory -> {
            if ("ALL".equalsIgnoreCase(boardGroupName) || boardGroup.getCategories().contains(boardCategory)) {
                return;
            }
            throw new BadRequestException("게시판 그룹과 카테고리가 일치하지 않습니다.");
        });
    }

    private Optional<BoardGroup> findBoardGroup(String boardGroupName) {
        try {
            return Optional.of(BoardGroup.fromValue(boardGroupName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private List<BoardCategory> getBoardCategories(List<String> boardCategoryNames) {
        if (CollectionUtils.isEmpty(boardCategoryNames)) {
            return List.of();
        }
        return boardCategoryNames
            .stream()
            .filter(StringUtils::isNotBlank)
            .map(BoardCategory::fromValue)
            .filter(Objects::nonNull)
            .toList();
    }
}
