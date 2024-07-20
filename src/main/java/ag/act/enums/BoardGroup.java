package ag.act.enums;

import ag.act.dto.boardcategory.BoardCategoryGroup;
import ag.act.dto.boardcategory.BoardCategoryGroups;
import ag.act.exception.BadRequestException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Slf4j
public enum BoardGroup {
    ANALYSIS("공지", false),
    ACTION("액션", false),
    DEBATE("토론방", false),
    GLOBALBOARD("전체게시판", true),
    GLOBALCOMMUNITY("자유게시판", true),
    GLOBALEVENT("이벤트/공지", true),
    ;

    private final String displayName;
    private final boolean isGlobal;

    BoardGroup(String displayName, boolean isGlobal) {
        this.displayName = displayName;
        this.isGlobal = isGlobal;
    }

    public List<BoardCategory> getCategories() {
        return Arrays.stream(BoardCategory.activeBoardCategories())
            .filter(boardCategory -> boardCategory.getBoardGroup() == this)
            .sorted(Comparator.comparingInt(BoardCategory::getDisplayOrder))
            .toList();
    }

    public static BoardGroup fromValue(String boardGroupName) {
        try {
            return BoardGroup.valueOf(boardGroupName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("존재하지 않는 게시판 그룹 이름입니다.", e);
        }
    }

    public static List<BoardGroup> getStockBoardGroups() {
        return Arrays.stream(BoardGroup.values())
            .filter(it -> !it.isGlobal())
            .toList();
    }

    public static List<BoardGroup> getGlobalBoardGroups() {
        return Arrays.stream(BoardGroup.values())
            .filter(BoardGroup::isGlobal)
            .toList();
    }

    public BoardCategoryGroups getBoardCategoryGroups() {
        return new BoardCategoryGroups(
            this.getCategories().stream()
                .filter(BoardCategory::isDisplayableInApp)
                .collect(Collectors.groupingBy(BoardCategory::getAvailableBoardCategoryCompanions, LinkedHashMap::new, Collectors.toList()))
                .keySet()
                .stream()
                .map(BoardCategoryGroup::new)
                .toList()
        );
    }

    public boolean isActionBoardGroup() {
        return this == ACTION;
    }
}
