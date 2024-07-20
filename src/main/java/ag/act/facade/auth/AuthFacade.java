package ag.act.facade.auth;

import ag.act.configuration.security.ActUserProvider;
import ag.act.configuration.security.CryptoHelper;
import ag.act.configuration.security.TokenProvider;
import ag.act.converter.mydata.MyDataTokenResponseConverter;
import ag.act.converter.user.AuthUserResponseConverter;
import ag.act.converter.user.UserConverter;
import ag.act.entity.User;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.InternalServerException;
import ag.act.exception.VerificationPinNumberException;
import ag.act.model.CanUseResponse;
import ag.act.model.CheckEmailResponse;
import ag.act.model.CmsLoginRequest;
import ag.act.model.PinNumberRequest;
import ag.act.model.RegisterUserInfoRequest;
import ag.act.module.mydata.MyDataService;
import ag.act.service.cms.CmsLoginService;
import ag.act.service.user.UserService;
import ag.act.service.user.UserVerificationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static ag.act.enums.ActErrorCode.PIN_VERIFICATION_FAILED;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private static final int MY_DATA_MAX_RETRY_COUNT = 3;
    private final UserService userService;
    private final MyDataService myDataService;
    private final UserVerificationHistoryService userVerificationHistoryService;
    private final UserConverter userConverter;
    private final MyDataTokenResponseConverter myDataTokenResponseConverter;
    private final AuthUserResponseConverter authUserResponseConverter;
    private final CryptoHelper cryptoHelper;
    private final TokenProvider tokenProvider;
    private final CmsLoginService cmsLoginService;

    public ag.act.model.UserResponse registerUserInfo(RegisterUserInfoRequest registerUserInfoRequest) {
        final User user = ActUserProvider.getNoneNull();
        return userConverter.convert(userService.registerUserInfo(user, registerUserInfoRequest));
    }

    public ag.act.model.UserResponse registerPinNumber(PinNumberRequest pinNumberRequest) {
        final User user = ActUserProvider.getNoneNull();
        return userConverter.convert(updatePinNumber(user, pinNumberRequest.getPinNumber()));
    }

    public ag.act.model.UserResponse verifyPinNumber(PinNumberRequest pinNumberRequest) {
        final User user = ActUserProvider.getNoneNull();
        return userConverter.convert(verifyAndUpdatePinNumber(user, pinNumberRequest.getPinNumber()));
    }

    private User updatePinNumber(User user, String pinNumber) {
        try {
            user.setHashedPinNumber(cryptoHelper.encrypt(pinNumber));
            user.setLastPinNumberVerifiedAt(LocalDateTime.now());
            userVerificationHistoryService.create(user.getId(), VerificationType.PIN, VerificationOperationType.REGISTER);
            return userService.saveUser(user);
        } catch (Exception e) {
            throw new InternalServerException("핀번호를 업데이트하는 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    private User verifyAndUpdatePinNumber(User user, String pinNumber) {
        try {
            if (!Objects.equals(user.getHashedPinNumber(), cryptoHelper.encrypt(pinNumber))) {
                throw new VerificationPinNumberException(PIN_VERIFICATION_FAILED.getCode(), "비밀번호가 일치하지 않습니다\n다시 입력해주세요");
            }

            user.setLastPinNumberVerifiedAt(LocalDateTime.now());
            final User savedUser = userService.saveUser(user);
            userVerificationHistoryService.create(user.getId(), VerificationType.PIN, VerificationOperationType.VERIFICATION);

            return savedUser;
        } catch (GeneralSecurityException e) {
            throw new VerificationPinNumberException(PIN_VERIFICATION_FAILED.getCode(), "비밀번호가 일치하지 않습니다\n다시 입력해주세요", e);
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("핀번호를 검증하는 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    public void logout() {
        final User user = ActUserProvider.getNoneNull();
        userService.invalidatePinNumberVerificationTime(user);
    }

    public ag.act.model.CheckEmailResponse checkEmail(String email) {
        final Optional<User> userByEmail = userService.getUserByEmail(email);

        boolean canUse = userByEmail.isEmpty() || (
            Objects.equals(userByEmail.get().getId(), ActUserProvider.getNoneNull().getId())
        );

        return new CheckEmailResponse().data(new CanUseResponse().canUse(canUse));
    }

    public ag.act.model.CheckNicknameResponse checkNickname(String nickname) {
        final Optional<User> userByNickname = userService.getUserByNickname(nickname);

        boolean canUse = userByNickname.isEmpty() || (
            Objects.equals(userByNickname.get().getId(), ActUserProvider.getNoneNull().getId())
        );

        return new ag.act.model.CheckNicknameResponse().data(new CanUseResponse().canUse(canUse));
    }

    public ag.act.model.SimpleStringResponse resetPinNumber() {
        userService.resetPinNumber(ActUserProvider.getNoneNull());
        return new ag.act.model.SimpleStringResponse().status("ok");
    }

    @SuppressWarnings("DefaultAnnotationParam")
    @Retryable(maxAttempts = MY_DATA_MAX_RETRY_COUNT, backoff = @Backoff(delay = 1000))
    public ag.act.model.MyDataTokenResponse requestMyDataToken(String accessToken) {
        final User user = ActUserProvider.getNoneNull();

        try {
            final String finpongAccessToken = getFinpongAccessToken(user, accessToken);
            return myDataTokenResponseConverter.convert(finpongAccessToken);
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("마이데이터 엑세스토큰 요청 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    private String getFinpongAccessToken(User user, String accessToken) {
        try {
            return myDataService.getFinpongAccessToken(
                user,
                cryptoHelper.decrypt(user.getHashedCI()),
                cryptoHelper.decrypt(user.getHashedPhoneNumber()),
                accessToken
            );
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("마이데이터 엑세스토큰 요청 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    public void withdrawMyDataService(User user) {
        final String finpongAccessToken = getFinpongAccessToken(user, "");
        withdrawMyDataService(finpongAccessToken);
    }

    public void withdrawMyDataService(String finpongAccessToken) {
        try {
            myDataService.withdraw(finpongAccessToken);
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ActRuntimeException("마이데이터 연동 취소 요청 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    public ag.act.model.AuthUserResponse loginInCms(CmsLoginRequest cmsLoginRequest) {
        final User user = cmsLoginService.login(cmsLoginRequest);

        return authUserResponseConverter.convert(
            user,
            tokenProvider.createWebToken(user.getId().toString()),
            null
        );
    }
}
