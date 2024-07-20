package ag.act.repository;

import ag.act.entity.User;
import ag.act.enums.admin.UserFilterType;
import ag.act.enums.admin.UserSearchType;
import ag.act.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryCustom {
    Page<User> findAllByConditions(
        List<Long> exclusiveUserIds,
        List<Status> exclusiveStatuses,
        UserFilterType userFilterType,
        UserSearchType userSearchType,
        String keyword,
        Pageable pageable
    );
}
