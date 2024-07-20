package ag.act.module.solidarity.election.assigner.tiebreaker.dto;

import ag.act.module.solidarity.election.ApplicantVote;

public interface TieBreakerDto<T> extends Comparable<TieBreakerDto<T>> {

    T getValue();

    boolean equals(Object otherValue);

    ApplicantVote getApplicantVote();
}
