package ag.act.service.user;

import ag.act.configuration.security.CryptoHelper;
import ag.act.dto.user.UserSearchFilterDto;
import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.repository.UserRepository;
import ag.act.repository.UserRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;

import java.util.List;
import java.util.stream.Stream;

import static ag.act.util.StatusUtil.getDeleteStatusesForUserList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserSearchService {
    private final UserRepositoryCustom userRepositoryCustom;
    private final CryptoHelper cryptoHelper;
    private final UserRepository userRepository;
    private final AdminUserService adminUserService;

    public Page<User> getUserList(UserSearchFilterDto userSearchFilterDto, Pageable pageable) {

        return userRepositoryCustom.findAllByConditions(
            getExclusiveUserIds(),
            getDeleteStatusesForUserList(),
            userSearchFilterDto.getUserFilterType(),
            userSearchFilterDto.getUserSearchType(),
            getKeyword(userSearchFilterDto),
            pageable
        );
    }

    private List<Long> getExclusiveUserIds() {
        return Stream.concat(
                userRepository.findAllCorporateUserIds().stream(),
                adminUserService.getSuperAdminUserIds().stream()
            )
            .toList();
    }

    @Nullable
    private String getKeyword(UserSearchFilterDto userSearchFilterDto) {
        final String keyword = userSearchFilterDto.getKeyword();

        if (StringUtils.isBlank(keyword)) {
            return keyword;
        }

        if (needEncryptKeyword(userSearchFilterDto)) {
            return getEncryptedKeyword(keyword);
        }

        return keyword;
    }

    private boolean needEncryptKeyword(UserSearchFilterDto userSearchFilterDto) {
        return userSearchFilterDto.getUserSearchType().needEncrypt();
    }

    private String getEncryptedKeyword(String keyword) {
        try {
            return cryptoHelper.encrypt(keyword);
        } catch (Exception e) {
            throw new BadRequestException("검색어를 확인해주세요.");
        }
    }

}
