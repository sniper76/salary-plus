package ag.act.module.solidarity.election.assigner.tiebreaker;

import ag.act.module.solidarity.election.ApplicantVote;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@RequiredArgsConstructor
public class TieBreakerResultHolder {
    private List<ApplicantVote> tempResult;

    public TieBreakerResultHolder(List<ApplicantVote> applicantVotes) {
        this.tempResult = new ArrayList<>(applicantVotes);
    }

    public boolean hasOnlyOneResult() {
        return tempResult.size() == 1;
    }

    public ApplicantVote getFirstItemOfResult() {
        return tempResult.get(0);
    }
}
