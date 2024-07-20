package ag.act.service.poll;

import ag.act.entity.Poll;
import ag.act.model.Status;
import ag.act.repository.PollRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PollService {
    private final PollRepository pollRepository;

    public Poll savePoll(Poll poll) {
        return pollRepository.save(poll);
    }

    public void deletePolls(List<Poll> polls, Status status, LocalDateTime deleteTime) {
        if (CollectionUtils.isEmpty(polls)) {
            return;
        }

        for (Poll poll : polls) {
            poll.setDeletedAt(deleteTime);
            poll.setStatus(status);

            savePoll(poll);
        }
    }
}
