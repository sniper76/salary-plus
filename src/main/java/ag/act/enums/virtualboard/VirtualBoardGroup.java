package ag.act.enums.virtualboard;

import ag.act.exception.BadRequestException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Getter
public enum VirtualBoardGroup {
    ACT_BEST("액트 베스트");

    private final String displayName;

    VirtualBoardGroup(String displayName) {
        this.displayName = displayName;
    }

    public static boolean isVirtualBoardGroupName(String boardGroupName) {
        return Arrays.stream(VirtualBoardGroup.values())
            .anyMatch(vbg -> vbg.name().equalsIgnoreCase(boardGroupName));
    }

    public static VirtualBoardGroup fromValue(String boardGroupName) {
        try {
            return VirtualBoardGroup.valueOf(boardGroupName.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("지원하지 않는 VirtualBoardGroupName %s 입니다.".formatted(boardGroupName));
        }
    }

    public List<VirtualBoardCategory> getVirtualCategories() {
        return Arrays.stream(VirtualBoardCategory.values())
            .filter(boardCategory -> boardCategory.getVirtualBoardGroup() == this)
            .sorted(Comparator.comparingInt(VirtualBoardCategory::getDisplayOrder))
            .toList();
    }
}
