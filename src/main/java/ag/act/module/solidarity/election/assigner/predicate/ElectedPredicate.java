package ag.act.module.solidarity.election.assigner.predicate;

import ag.act.module.solidarity.election.ApplicantVote;
import ag.act.module.solidarity.election.ISolidarityLeaderElection;

import java.util.function.BiPredicate;

public interface ElectedPredicate extends BiPredicate<Long, ApplicantVote>, ISolidarityLeaderElection.ElectionCondition {
}
