package ag.act.enums;

import ag.act.core.configuration.DevServerFeatureFlagUtil;
import ag.act.dto.boardcategory.IBoardCategory;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
public enum BoardCategory implements IBoardCategory {
    DAILY_ACT("분석자료", BoardGroup.ANALYSIS, true, 0),
    STOCK_CURRENT_ISSUES("종목현안", BoardGroup.ANALYSIS, false, 1),
    TOPICS("주담통", BoardGroup.ANALYSIS, false, 2),
    WEEKLY_ISSUES("주간이슈", BoardGroup.ANALYSIS, false, 3),
    ANALYZE_REPORTS("리포트 분석", BoardGroup.ANALYSIS, false, 4),
    SOLIDARITY_LEADER_LETTERS("주주연대 공지", BoardGroup.ANALYSIS, true, 5),
    SOLIDARITY_LEADER_ELECTION("대표선출", BoardGroup.ANALYSIS, true, 6) {
        public boolean isDisplayableInApp() {
            return false;
        }

        public boolean isEditable() {
            return false;
        }

        public boolean isCreatable() {
            return false;
        }

        public boolean isDeletable() {
            return false;
        }
    },
    HOLDER_LIST_READ_AND_COPY("주주명부 열람/등사", BoardGroup.ANALYSIS, true, 7) {
        public boolean isOnlyExclusiveToHolders() {
            return true;
        }

        public boolean isCreatableFromCms() {
            return false;
        }

        public boolean isEditable() {
            return false;
        }

        public boolean isDeletable() {
            return false;
        }
    },

    DEBATE("토론방", BoardGroup.DEBATE, true, 0),

    SURVEYS("설문", BoardGroup.ACTION, true, 0),
    DIGITAL_DELEGATION("의결권위임", BoardGroup.ACTION, true, 1),
    CO_HOLDING_ARRANGEMENTS("공동보유", BoardGroup.ACTION, true, 2),
    ETC("10초 서명", BoardGroup.ACTION, true, 3),
    TEST_FOR_DEV("개발서버카테고리", BoardGroup.ACTION, true, 1000) {
        public boolean isDisplayableInApp() {
            return DevServerFeatureFlagUtil.isDisplayableInApp(this);
        }
    },

    STOCKHOLDER_ACTION("주주행동", BoardGroup.GLOBALBOARD, true, 0),
    STOCK_ANALYSIS_DATA("분석자료", BoardGroup.GLOBALBOARD, true, 1),

    FREE_DEBATE("자유게시판", BoardGroup.GLOBALCOMMUNITY, true, 0),

    EVENT("이벤트", BoardGroup.GLOBALEVENT, true, 0) {
        @Override
        public List<BoardCategory> getAvailableBoardCategoryCompanions() {
            return List.of(EVENT, CAMPAIGN);
        }

        @Override
        public Optional<String> getBadgeColor() {
            return Optional.of("FF910A");
        }

    },
    CAMPAIGN("캠페인", BoardGroup.GLOBALEVENT, true, 1) {
        @Override
        public List<BoardCategory> getAvailableBoardCategoryCompanions() {
            return List.of(EVENT, CAMPAIGN);
        }

        @Override
        public Optional<String> getBadgeColor() {
            return Optional.of("67C91C");
        }

    },
    NOTICE("공지사항", BoardGroup.GLOBALEVENT, true, 2) {
        @Override
        public Optional<String> getBadgeColor() {
            return Optional.of("355CE9");
        }
    },
    ;

    private final String displayName;
    private final BoardGroup boardGroup;
    private final boolean active;
    private final int displayOrder;

    BoardCategory(String displayName, BoardGroup boardGroup, boolean active, int displayOrder) {
        this.displayName = displayName;
        this.boardGroup = boardGroup;
        this.active = active;
        this.displayOrder = displayOrder;
    }

    public String getName() {
        return this.name();
    }

    public boolean isDisplayableInApp() {
        return true;
    }

    public boolean isEditable() {
        return true;
    }

    public boolean isCreatable() {
        return true;
    }

    public boolean isCreatableFromCms() {
        return true;
    }

    public boolean isDeletable() {
        return true;
    }

    public static BoardCategory[] activeBoardCategories() {
        return Arrays.stream(BoardCategory.values())
            .filter(boardCategory -> boardCategory.active)
            .toArray(BoardCategory[]::new);
    }

    public static BoardCategory[] activeBoardCategoriesForStocks() {
        return Arrays.stream(BoardCategory.values())
            .filter(boardCategory -> !boardCategory.getBoardGroup().isGlobal())
            .filter(boardCategory -> boardCategory.active)
            .toArray(BoardCategory[]::new);
    }

    public static BoardCategory[] activeBoardCategoriesForGlobalBoard() {
        return Arrays.stream(BoardCategory.values())
            .filter(boardCategory -> boardCategory.getBoardGroup().isGlobal())
            .filter(boardCategory -> boardCategory.active)
            .toArray(BoardCategory[]::new);
    }

    public boolean isLeaderWritable() {
        return getLeaderWritableBoardCategories().contains(this);
    }

    public boolean isAllUserWritable() {
        return getAllUserWritableBoardCategories().contains(this);
    }

    public boolean isOnlyExclusiveToHolders() {
        return false;
    }

    public static List<BoardCategory> getLeaderWritableBoardCategories() {
        // 주주대표 글쓰기 권한은 종목게시판의 게시판(주주연대공지/분석자료)와 액션(설문, 주주명부 열람/등사)
        return List.of(SOLIDARITY_LEADER_LETTERS, DAILY_ACT, SURVEYS, HOLDER_LIST_READ_AND_COPY);
    }

    public static List<BoardCategory> getDigitalDocumentCampaignBoardCategories() {
        return List.of(SURVEYS, ETC);
    }

    public static List<BoardCategory> getAllUserWritableBoardCategories() {
        return List.of(DEBATE, FREE_DEBATE);
    }

    public boolean isDigitalDocumentActionGroup() {
        return BoardGroup.ACTION.getCategories()
            .stream()
            .filter(category -> category != BoardCategory.SURVEYS)
            .anyMatch(category -> category == this);
    }

    public static BoardCategory fromValue(String boardCategoryName) {
        if (boardCategoryName == null) {
            return null;
        }

        try {
            final BoardCategory boardCategory = BoardCategory.valueOf(boardCategoryName.toUpperCase());
            if (!boardCategory.active) {
                return null;
            }
            return boardCategory;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Optional<BoardCategory> findBoardCategory(String boardCategoryName) {
        return Optional.ofNullable(fromValue(boardCategoryName));
    }

    public List<BoardCategory> getAvailableBoardCategoryCompanions() {
        return List.of(this);
    }

    public Optional<String> getBadgeColor() {
        return Optional.empty();
    }
}
