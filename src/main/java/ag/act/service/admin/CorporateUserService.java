package ag.act.service.admin;


import ag.act.dto.admin.GetCorporateUsersSearchDto;
import ag.act.entity.CorporateUser;
import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.model.CorporateUserRequest;
import ag.act.model.Status;
import ag.act.repository.CorporateUserRepository;
import ag.act.repository.SolidarityLeaderRepository;
import ag.act.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CorporateUserService {
    private static final String CORPORATE_USER_EXISTS_MESSAGE = "이미 존재하는 법인사업자입니다.";
    private final UserService userService;
    private final CorporateUserRepository corporateUserRepository;
    private final SolidarityLeaderRepository solidarityLeaderRepository;

    public CorporateUser create(CorporateUserRequest corporateUserRequest, User user) {
        validateForCreate(corporateUserRequest);

        final CorporateUser corporateUser = new CorporateUser();
        corporateUser.setUserId(user.getId());
        corporateUser.setCorporateName(corporateUserRequest.getCorporateName());
        corporateUser.setCorporateNo(corporateUserRequest.getCorporateNo());
        corporateUser.setStatus(Status.ACTIVE);

        return corporateUserRepository.save(corporateUser);
    }

    public Page<CorporateUser> getCorporateUsers(GetCorporateUsersSearchDto getCorporateUsersSearchDto) {
        if (getCorporateUsersSearchDto.isBlankSearchKeyword()) {
            return corporateUserRepository.findAll(
                getCorporateUsersSearchDto.getPageRequest()
            );
        }

        return corporateUserRepository.findAllByCorporateNameContaining(
            getCorporateUsersSearchDto.getSearchKeyword(),
            getCorporateUsersSearchDto.getPageRequest()
        );
    }

    public void deleteCorporateUser(Long corporateId) {
        final CorporateUser corporateUser = getCorporateUser(corporateId);

        if (isSolidarityLeader(corporateUser)) {
            throw new BadRequestException("이미 주주대표로 선정되어 있는 법인사업자입니다.");
        }

        deleteCorporateUser(corporateUser);
        deleteUser(corporateUser.getUserId());
    }

    private void deleteCorporateUser(CorporateUser corporateUser) {
        corporateUser.setStatus(Status.DELETED_BY_ADMIN);
        corporateUserRepository.save(corporateUser);
    }

    private void deleteUser(Long userId) {
        final User user = userService.getUser(userId);
        user.setStatus(Status.DELETED_BY_ADMIN);
        userService.saveUser(user);
    }

    public CorporateUser updateCorporateUser(Long corporateId, CorporateUserRequest corporateUserRequest) {
        final CorporateUser corporateUser = getCorporateUser(corporateId);
        final String corporateNo = corporateUserRequest.getCorporateNo().trim();

        validateForUpdate(corporateUser, corporateNo);

        corporateUser.setCorporateNo(corporateNo);
        corporateUser.setCorporateName(corporateUserRequest.getCorporateName());

        return corporateUserRepository.save(corporateUser);
    }

    private void validateForUpdate(CorporateUser corporateUser, String corporateNo) {
        if (!isSameCorporateNo(corporateUser, corporateNo)
            && existCorporateUserByCorporateNo(corporateNo)
        ) {
            throw new BadRequestException(CORPORATE_USER_EXISTS_MESSAGE);
        }
    }

    private boolean isSameCorporateNo(CorporateUser corporateUser, String corporateNo) {
        return Objects.equals(corporateUser.getCorporateNo(), corporateNo);
    }

    private void validateForCreate(CorporateUserRequest corporateUserRequest) {
        if (existCorporateUserByCorporateNo(corporateUserRequest.getCorporateNo())) {
            throw new BadRequestException(CORPORATE_USER_EXISTS_MESSAGE);
        }
    }

    public String getNullableCorporateNoByUserId(Long userId) {
        return findCorporateUserByUserId(userId)
            .map(CorporateUser::getCorporateNo)
            .orElse(null);
    }

    public CorporateUser getCorporateUser(Long corporateUserId) {
        return corporateUserRepository.findById(corporateUserId)
            .orElseThrow(() -> new BadRequestException("법인 사업자 정보가 존재하지 않습니다."));
    }

    public Optional<CorporateUser> findCorporateUserByUserId(Long userId) {
        return corporateUserRepository.findByUserIdAndStatus(userId, Status.ACTIVE);
    }

    private Optional<CorporateUser> findCorporateUserByCorporateNo(String corporateNo) {
        return corporateUserRepository.findByCorporateNo(corporateNo);
    }

    private boolean existCorporateUserByCorporateNo(String corporateNo) {
        return findCorporateUserByCorporateNo(corporateNo).isPresent();
    }

    private boolean isSolidarityLeader(CorporateUser corporateUser) {
        return solidarityLeaderRepository.existsByUserId(corporateUser.getUserId());
    }
}
