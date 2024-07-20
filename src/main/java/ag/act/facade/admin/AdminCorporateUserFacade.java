package ag.act.facade.admin;

import ag.act.configuration.security.CryptoHelper;
import ag.act.converter.CorporateUserConverter;
import ag.act.converter.CorporateUserDetailsResponseConverter;
import ag.act.converter.PageDataConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.admin.GetCorporateUsersSearchDto;
import ag.act.entity.CorporateUser;
import ag.act.entity.User;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.BadRequestException;
import ag.act.model.CorporateUserDataResponse;
import ag.act.model.CorporateUserRequest;
import ag.act.model.Gender;
import ag.act.model.GetCorporateUserDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.service.NicknameHistoryService;
import ag.act.service.admin.CorporateUserService;
import ag.act.service.user.UserService;
import ag.act.util.SimpleStringResponseUtil;
import ag.act.validator.user.CorporateUserValidator;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AdminCorporateUserFacade {
    private final UserService userService;
    private final CorporateUserService corporateUserService;
    private final NicknameHistoryService nicknameHistoryService;
    private final CryptoHelper cryptoHelper;
    private final PageDataConverter pageDataConverter;
    private final CorporateUserValidator corporateUserValidator;
    private final CorporateUserConverter corporateUserConverter;
    private final CorporateUserDetailsResponseConverter corporateUserDetailsResponseConverter;

    public CorporateUserDataResponse createCorporateUser(CorporateUserRequest corporateUserRequest) {
        corporateUserValidator.validateCorporateNo(corporateUserRequest.getCorporateNo().trim());

        final CorporateUser corporateUser = corporateUserService.create(corporateUserRequest, createUser(corporateUserRequest));

        return new CorporateUserDataResponse().data(
            corporateUserDetailsResponseConverter.convert(corporateUser)
        );
    }

    private User createUser(CorporateUserRequest corporateUserRequest) {
        try {
            final User user = userService.saveUser(createUserForCorporateUser(corporateUserRequest));
            nicknameHistoryService.create(user);

            return user;
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("이미 존재하는 법인사업자입니다.", ex);
        }
    }

    @NotNull
    private User createUserForCorporateUser(CorporateUserRequest corporateUserRequest) {

        final String userEmail = "%s@act.ag".formatted(corporateUserRequest.getCorporateNo());
        final String corporateName = corporateUserRequest.getCorporateName().trim();

        final User user = new User();
        user.setName(corporateName);
        user.setNickname(corporateName);
        user.setEmail(userEmail);
        user.setGender(Gender.F);
        user.setBirthDate(LocalDateTime.now());
        user.setStatus(Status.ACTIVE);

        try {
            user.setHashedPhoneNumber(cryptoHelper.encrypt(userEmail));
            user.setHashedCI(cryptoHelper.encrypt(userEmail));
            user.setHashedDI(cryptoHelper.encrypt(userEmail));
        } catch (Exception ex) {
            throw new ActRuntimeException("법인사업자 생성중에 알 수 없는 오류가 발생했습니다.", ex);
        }
        return user;
    }

    public GetCorporateUserDataResponse getCorporateUsers(GetCorporateUsersSearchDto getCorporateUsersSearchDto) {
        final Page<CorporateUser> corporateUserPage = corporateUserService.getCorporateUsers(getCorporateUsersSearchDto);

        return pageDataConverter.convert(
            new SimplePageDto<>(corporateUserPage.map(corporateUserConverter)),
            GetCorporateUserDataResponse.class
        );
    }

    public CorporateUserDataResponse updateCorporateUser(Long corporateId, CorporateUserRequest corporateUserRequest) {
        corporateUserValidator.validateCorporateNo(corporateUserRequest.getCorporateNo().trim());

        return new CorporateUserDataResponse().data(
            corporateUserDetailsResponseConverter.convert(
                corporateUserService.updateCorporateUser(corporateId, corporateUserRequest)
            )
        );
    }

    public SimpleStringResponse deleteCorporateUser(Long corporateId) {
        corporateUserService.deleteCorporateUser(corporateId);
        return SimpleStringResponseUtil.ok();
    }
}
