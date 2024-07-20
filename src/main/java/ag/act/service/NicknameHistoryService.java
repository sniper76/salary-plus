package ag.act.service;

import ag.act.entity.NicknameHistory;
import ag.act.entity.User;
import ag.act.model.Status;
import ag.act.repository.NicknameHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NicknameHistoryService {
    private final NicknameHistoryRepository nicknameHistoryRepository;

    public NicknameHistory create(User user) {
        deleteAllPreviousNicknameHistories(user);

        final NicknameHistory nicknameHistory = new NicknameHistory();
        nicknameHistory.setUserId(user.getId());
        nicknameHistory.setNickname(user.getNickname());
        nicknameHistory.setIsFirst(isFirstNickname(user));
        nicknameHistory.setStatus(Status.ACTIVE);
        user.setNicknameHistory(nicknameHistory);

        return nicknameHistoryRepository.save(nicknameHistory);
    }

    public NicknameHistory createByAdmin(User user) {
        deleteAllPreviousNicknameHistoriesByAdmin(user);

        final NicknameHistory nicknameHistory = new NicknameHistory();
        nicknameHistory.setUserId(user.getId());
        nicknameHistory.setNickname(user.getNickname());
        nicknameHistory.setIsFirst(isFirstNickname(user));
        nicknameHistory.setStatus(Status.ACTIVE);
        nicknameHistory.setByAdmin(true);
        user.setNicknameHistory(nicknameHistory);

        return nicknameHistoryRepository.save(nicknameHistory);
    }

    private boolean isFirstNickname(User user) {
        return nicknameHistoryRepository.countByUserId(user.getId()) == 0;
    }

    private void deleteAllPreviousNicknameHistories(User user) {
        final List<NicknameHistory> nicknameHistoriesForDelete = nicknameHistoryRepository.findAllByUserIdAndStatus(user.getId(), Status.ACTIVE)
            .stream()
            .map(this::setInactiveByUser)
            .toList();

        nicknameHistoryRepository.saveAll(nicknameHistoriesForDelete);
    }

    private void deleteAllPreviousNicknameHistoriesByAdmin(User user) {
        final List<NicknameHistory> nicknameHistoriesForDelete = nicknameHistoryRepository.findAllByUserIdAndStatus(user.getId(), Status.ACTIVE)
            .stream()
            .map(this::setInactiveByAdmin)
            .toList();

        nicknameHistoryRepository.saveAll(nicknameHistoriesForDelete);
    }

    private NicknameHistory setInactiveByUser(NicknameHistory nicknameHistory) {
        nicknameHistory.setDeletedAt(LocalDateTime.now());
        nicknameHistory.setStatus(Status.INACTIVE_BY_USER);

        return nicknameHistory;
    }

    private NicknameHistory setInactiveByAdmin(NicknameHistory nicknameHistory) {
        nicknameHistory.setDeletedAt(LocalDateTime.now());
        nicknameHistory.setStatus(Status.INACTIVE_BY_ADMIN);

        return nicknameHistory;
    }

    public void deleteAllByUserId(Long userId) {
        nicknameHistoryRepository.deleteAllByUserId(userId);
    }
}
