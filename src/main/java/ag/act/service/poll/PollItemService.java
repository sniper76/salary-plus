package ag.act.service.poll;

import ag.act.entity.PollItem;
import ag.act.repository.PollItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PollItemService {
    private final PollItemRepository pollItemRepository;

    public PollItemService(PollItemRepository pollItemRepository) {
        this.pollItemRepository = pollItemRepository;
    }

    public List<PollItem> getPollItems(Long pollId) {
        return pollItemRepository.findAllByPollId(pollId);
    }
}
