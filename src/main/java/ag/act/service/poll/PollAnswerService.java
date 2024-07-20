package ag.act.service.poll;

import ag.act.entity.PollAnswer;
import ag.act.model.Status;
import ag.act.repository.PollAnswerRepository;
import ag.act.repository.interfaces.JoinCount;
import ag.act.repository.interfaces.PollItemCount;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class PollAnswerService {
    private final PollAnswerRepository pollAnswerRepository;

    public List<PollAnswer> getAllByPollIdAndUserId(Long pollId, Long userId) {
        return pollAnswerRepository.findAllByPollIdAndUserId(pollId, userId);
    }

    public List<PollAnswer> getAllByPostIdAndUserId(Long postId, Long userId) {
        return pollAnswerRepository.findAllByPostIdAndUserId(postId, userId);
    }

    public List<PollAnswer> getAllByPollId(Long pollId) {
        return pollAnswerRepository.findAllByPollId(pollId);
    }

    public List<PollAnswer> getPollAnswersByPostId(Long postId) {
        return pollAnswerRepository.findPollAnswersByPostId(postId);
    }

    public long sumApprovalPollAnswersByPostId(Long postId, Long solidarityLeaderApplicantId) {
        return pollAnswerRepository.sumApprovalPollAnswersByPostId(postId, solidarityLeaderApplicantId);
    }

    public List<PollItemCount> getPollItemCountsByPollItemId(Long pollId) {
        return pollAnswerRepository.findPollItemCountsByPollId(pollId);
    }

    public List<PollItemCount> getPollItemCountsByPostId(Long postId) {
        return pollAnswerRepository.findPollItemCountsByPostId(postId);
    }

    public Optional<JoinCount> findPollCountByPostId(Long postId) {
        return pollAnswerRepository.findPollCountByPostId(postId);
    }

    public List<PollAnswer> saveAll(List<PollAnswer> saveList) {
        return pollAnswerRepository.saveAll(saveList);
    }

    public void deleteAll(List<PollAnswer> deleteList) {
        pollAnswerRepository.deleteAll(deleteList);
    }

    public PollAnswer makePollAnswer(Long pollId, Long userId, Long pollItemId, Long stockQuantity) {
        PollAnswer pollAnswer = new PollAnswer();
        pollAnswer.setPollId(pollId);
        pollAnswer.setUserId(userId);
        pollAnswer.setPollItemId(pollItemId);
        pollAnswer.setStatus(Status.ACTIVE);
        pollAnswer.setStockQuantity(stockQuantity);
        return pollAnswer;
    }

    public long getTotalVoteStockQuantityByPostId(Long postId) {
        return getTotalVoteStockQuantity(pollAnswerRepository.findPollAnswersByPostId(postId));
    }

    public long getTotalVoteStockQuantity(List<PollAnswer> pollAnswers) {
        long res = 0;

        for (PollAnswer pollAnswer : pollAnswers) {
            res += pollAnswer.getStockQuantity();
        }

        return res;
    }

    public Map<Long, List<PollItemCount>> getVoteItemListMap(Long postId) {
        return getPollItemCountsByPostId(postId)
            .stream()
            .collect(Collectors.groupingBy(PollItemCount::getPollId));
    }

    public Map<Long, List<PollAnswer>> getAllAnswerListMap(Long postId) {
        return getPollAnswersByPostId(postId)
            .stream()
            .collect(Collectors.groupingBy(PollAnswer::getPollId));
    }

    public Map<Long, List<PollAnswer>> getAnswerListMap(Long postId, Long currentUserId) {
        return getAllByPostIdAndUserId(postId, currentUserId)
            .stream()
            .collect(Collectors.groupingBy(PollAnswer::getPollId));
    }
}
