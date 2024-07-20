package ag.act.service.blockeduser;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public interface BlockedUserEnhancer {
    List<Long> EMPTY_BLOCKED_USER_ID_FOR_ALL_LIST = List.of(-1L);

    default List<Long> refinedBlockedUserIdList(List<Long> blockedUserIdList) {
        return CollectionUtils.isEmpty(blockedUserIdList) ? EMPTY_BLOCKED_USER_ID_FOR_ALL_LIST : blockedUserIdList;
    }
}
