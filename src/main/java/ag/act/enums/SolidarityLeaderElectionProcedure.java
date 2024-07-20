package ag.act.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("LineLength")
@Getter
public enum SolidarityLeaderElectionProcedure {
    APPLY_BEGIN(0, "주주대표 후보자 최초 지원(모집 개설)", 0, "* 주주대표 지원자가 나오면 지원자 모집이 시작됩니다."),
    RECRUIT_APPLICANTS(1, "지원자 모집(4일)", 4, "* 모든 지원자가 지원을 취소할 경우 선출과정이 초기화됩니다."),
    START_VOTING(2, "주주대표 선출 투표 시작(3일)", 3, "* 선출기준\n- 투표시작시점 기준, 찬성표가 액트 내 결집 지분의 25% 이상 & 반대표를 초과시.\n- 투표시작시점 기준, 찬성표가 액트 내 결집 지분의 50% 초과시(조기마감)."),
    CLOSE_VOTING(3, "주주대표 선출 투표 마감", 0, null),
    LEADER_ELECTED(4, "주주대표 선출", 0, null);

    private final int displayOrder;
    private final String title;
    private final int durationDays;
    private final String description;

    public static List<SolidarityLeaderElectionProcedure> getSortedProcedures() {
        return Arrays.stream(values())
            .sorted(Comparator.comparing(SolidarityLeaderElectionProcedure::getDisplayOrder))
            .toList();
    }
}
