package ag.act.module.solidarity.election.assigner.tiebreaker.filter;

import ag.act.module.solidarity.election.ApplicantVote;

import java.util.List;

public interface TieBreakerFilter {
    List<ApplicantVote> filter(List<ApplicantVote> tieApplicantVoteList);
}
