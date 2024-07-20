package ag.act.module.solidarity.election.assigner.tiebreaker.dto;

import ag.act.module.solidarity.election.ApplicantVote;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UserRegistrationDateTieBreakerDto implements TieBreakerDto<LocalDateTime> {
    @Getter
    private final ApplicantVote applicantVote;
    private final LocalDateTime registrationDateTime;

    @Override
    public int compareTo(TieBreakerDto<LocalDateTime> other) {
        return sortAsc(other);
    }

    private int sortAsc(TieBreakerDto<LocalDateTime> other) {
        return this.getValue().compareTo(other.getValue());
    }

    @Override
    public LocalDateTime getValue() {
        return registrationDateTime;
    }

    @Override
    public boolean equals(Object otherValue) {
        return registrationDateTime.equals(otherValue);
    }
}
