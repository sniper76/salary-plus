package ag.act.module.solidarity.election.assigner.tiebreaker.dto;

import ag.act.module.solidarity.election.ApplicantVote;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public class NumberOfPostsTieBreakerDto implements TieBreakerDto<Long> {
    @Getter
    private final ApplicantVote applicantVote;
    private final Long numberOfPosts;

    @Override
    public int compareTo(TieBreakerDto<Long> other) {
        return sortDesc(other);
    }

    private int sortDesc(TieBreakerDto<Long> other) {
        return other.getValue().compareTo(this.getValue());
    }

    @Override
    public Long getValue() {
        return numberOfPosts;
    }

    @Override
    public boolean equals(Object otherValue) {
        return Objects.equals(numberOfPosts, otherValue);
    }
}
