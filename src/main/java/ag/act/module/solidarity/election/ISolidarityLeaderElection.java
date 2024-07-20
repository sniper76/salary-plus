package ag.act.module.solidarity.election;

import ag.act.constants.ActColors;

public interface ISolidarityLeaderElection {

    interface ElectionCondition {
        String RESOLUTION_CONDITION_LABEL = "결의요건";
        String EARLY_FINISHED_LABEL = "조기마감";
        String COLOR_FOR_UNSATISFIED_CONDITION = ActColors.COLOR_RED;
        String COLOR_FOR_SATISFIED_CONDITION = ActColors.COLOR_GREEN;
        String UNIT = "주";
        int ONE_FOURTH_DIVISOR = 4;
        int EARLY_ELECTION_DIVISOR = 2;
        String REQUIRED_STOCK_QUANTITY_RATIO = "1/" + ONE_FOURTH_DIVISOR;
    }

    interface ElectionDetailLabel {
        String COLOR_FOR_COMPLETE = ActColors.COLOR_GREEN;
        String COLOR_FOR_PROCESSING = ActColors.COLOR_RED;
        String CLOSING_LABEL = "마감";
        String APPLICANT_TITLE = "지원자 현황";
        String UNIT = "명";
    }

    interface ElectionPost {
        String POST_TITLE = "주주대표 선출 투표";
        String POST_CONTENT = "";
    }
}
