package ag.act.enums.virtualboard;

import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Getter
public enum VirtualBoardCategory {
    ACT_BEST_STOCK("종목", VirtualBoardGroup.ACT_BEST, true, 1) {
        @Override
        public List<BoardCategory> getSubCategories() {
            List<BoardCategory> analysisCategories = BoardGroup.ANALYSIS.getCategories();
            List<BoardCategory> debateCategories = BoardGroup.DEBATE.getCategories();

            return Stream.concat(analysisCategories.stream(), debateCategories.stream())
                .toList();
        }

        @Override
        public boolean supportExclusiveToHolders() {
            return true;
        }
    },
    ACT_BEST_SHAREHOLDER_ACTION_NEWS("주주행동 News", VirtualBoardGroup.ACT_BEST, true, 2) {
        @Override
        public List<BoardCategory> getSubCategories() {
            return BoardGroup.GLOBALBOARD.getCategories();
        }

        @Override
        public boolean supportExclusiveToHolders() {
            return false;
        }
    },
    ACT_BEST_COMMUNITY("자유게시판", VirtualBoardGroup.ACT_BEST, true, 3) {
        @Override
        public List<BoardCategory> getSubCategories() {
            return BoardGroup.GLOBALCOMMUNITY.getCategories();
        }

        @Override
        public boolean supportExclusiveToHolders() {
            return false;
        }
    };

    private final String displayName;
    private final VirtualBoardGroup virtualBoardGroup;
    private final boolean active;
    private final int displayOrder;

    VirtualBoardCategory(String displayName, VirtualBoardGroup virtualBoardGroup, boolean active, int displayOrder) {
        this.displayName = displayName;
        this.virtualBoardGroup = virtualBoardGroup;
        this.active = active;
        this.displayOrder = displayOrder;
    }

    public abstract List<BoardCategory> getSubCategories();

    public abstract boolean supportExclusiveToHolders();

    private static Optional<VirtualBoardCategory> fromValue(String categoryName) {
        try {
            return Optional.of(VirtualBoardCategory.valueOf(categoryName.toUpperCase()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static List<VirtualBoardCategory> fromValues(List<String> categoryNames) {
        return emptyIfNull(categoryNames)
            .stream()
            .map(VirtualBoardCategory::fromValue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    public static boolean isActBestStockBoardCategory(String boardCategoryName) {
        return fromValue(boardCategoryName)
            .map(VirtualBoardCategory::isActBestStock)
            .orElse(false);
    }

    private boolean isActBestStock() {
        return this == VirtualBoardCategory.ACT_BEST_STOCK;
    }

    public static List<BoardCategory> getBoardCategories(List<String> boardCategoryNames) {
        return VirtualBoardCategory.fromValues(boardCategoryNames)
            .stream()
            .map(VirtualBoardCategory::getSubCategories)
            .flatMap(List::stream)
            .toList();
    }
}
