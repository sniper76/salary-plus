package ag.act.facade.auth;

import ag.act.configuration.security.ActUserProvider;
import ag.act.configuration.security.CryptoHelper;
import ag.act.configuration.security.TokenProvider;
import ag.act.converter.kcb.KcbOkCertRequestConverter;
import ag.act.converter.kcb.KcbOkCertResponseConverter;
import ag.act.converter.user.AuthUserResponseConverter;
import ag.act.entity.User;
import ag.act.enums.Nationality;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.InternalServerException;
import ag.act.model.ResendAuthRequest;
import ag.act.model.SendAuthRequest;
import ag.act.model.Status;
import ag.act.model.VerifyAuthCodeRequest;
import ag.act.module.okcert.dto.OkCertResendRequest;
import ag.act.module.okcert.dto.OkCertResendResponse;
import ag.act.module.okcert.dto.OkCertSendRequest;
import ag.act.module.okcert.dto.OkCertSendResponse;
import ag.act.module.okcert.dto.OkCertVerifyRequest;
import ag.act.module.okcert.dto.OkCertVerifyResponse;
import ag.act.parser.DateTimeParser;
import ag.act.service.KcbService;
import ag.act.service.user.UserService;
import ag.act.service.user.UserVerificationHistoryService;
import ag.act.util.NumberUtil;
import ag.act.validator.user.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SmsAuthFacade {
    private static final String COMMON_ERROR_MESSAGE = "인증서버의 응답을 처리하는 중에 알 수 없는 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.";
    private final KcbService kcbService;
    private final UserService userService;
    private final UserVerificationHistoryService userVerificationHistoryService;
    private final TokenProvider tokenProvider;
    private final CryptoHelper cryptoHelper;
    private final PasswordEncoder passwordEncoder;
    private final AuthUserResponseConverter authUserResponseConverter;
    private final KcbOkCertRequestConverter kcbOkCertRequestConverter;
    private final KcbOkCertResponseConverter kcbOkCertResponseConverter;
    private final UserValidator userValidator;

    public SmsAuthFacade(
        KcbService kcbService,
        UserService userService,
        UserVerificationHistoryService userVerificationHistoryService,
        TokenProvider tokenProvider,
        CryptoHelper cryptoHelper,
        PasswordEncoder passwordEncoder,
        AuthUserResponseConverter authUserResponseConverter,
        KcbOkCertRequestConverter kcbOkCertRequestConverter,
        KcbOkCertResponseConverter kcbOkCertResponseConverter,
        UserValidator userValidator
    ) {
        this.kcbService = kcbService;
        this.userService = userService;
        this.userVerificationHistoryService = userVerificationHistoryService;
        this.tokenProvider = tokenProvider;
        this.cryptoHelper = cryptoHelper;
        this.passwordEncoder = passwordEncoder;
        this.authUserResponseConverter = authUserResponseConverter;
        this.kcbOkCertRequestConverter = kcbOkCertRequestConverter;
        this.kcbOkCertResponseConverter = kcbOkCertResponseConverter;
        this.userValidator = userValidator;
    }

    public ag.act.model.SendAuthResponse sendAuthRequest(ag.act.model.SendAuthRequest sendAuthRequest) {
        userValidator.validateStatus(ActUserProvider.get());

        try {
            final OkCertSendResponse responseDto = kcbService.sendAuthRequest(toRequestDto(sendAuthRequest));
            return toResponse(responseDto);
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(COMMON_ERROR_MESSAGE, e);
        }
    }

    public ag.act.model.ResendAuthResponse resendAuthRequest(ResendAuthRequest resendAuthRequest) {
        userValidator.validateStatus(ActUserProvider.get());

        try {
            final OkCertResendResponse responseDto = kcbService.resendAuthRequest(toRequestDto(resendAuthRequest));
            return toResponse(responseDto);
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(COMMON_ERROR_MESSAGE, e);
        }
    }

    public ag.act.model.AuthUserResponse verifyAuthCode(VerifyAuthCodeRequest verifyAuthCodeRequest) {
        userValidator.validateStatus(ActUserProvider.get());

        try {
            final OkCertVerifyResponse responseDto = kcbService.verifyAuthCode(toRequestDto(verifyAuthCodeRequest));

            final String hashedCI = cryptoHelper.encrypt(responseDto.getCi());
            final String hashedPhoneNumber = cryptoHelper.encrypt(verifyAuthCodeRequest.getPhoneNumber());

            final Optional<User> userOptional = userService.getUserByHashedCI(hashedCI);
            userValidator.validateStatus(userOptional);

            final User user = userOptional.orElseGet(() -> {
                final String hashedDI = passwordEncoder.encode(responseDto.getDi());
                return createUser(verifyAuthCodeRequest, hashedCI, hashedDI, hashedPhoneNumber);
            });

            userVerificationHistoryService.create(user.getId(), VerificationType.SMS, VerificationOperationType.REGISTER);

            final String accessToken = generateAccessToken(user.getId());

            return authUserResponseConverter.convert(user, accessToken, null);
        } catch (ActRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException(COMMON_ERROR_MESSAGE, e);
        }
    }

    private String generateAccessToken(Long id) {
        return tokenProvider.createAppToken(id.toString());
    }

    private User createUser(VerifyAuthCodeRequest verifyAuthCodeRequest, String hashedCI, String hashedDI, String hashedPhoneNumber) {
        User user = new User();
        user.setHashedPhoneNumber(hashedPhoneNumber);
        user.setBirthDate(DateTimeParser.parseDate(verifyAuthCodeRequest.getBirthDate()));
        user.setGender(ag.act.model.Gender.valueOf(verifyAuthCodeRequest.getGender()));
        user.setName(getTrimmedUserName(verifyAuthCodeRequest));
        user.setFirstNumberOfIdentification(NumberUtil.toInteger(verifyAuthCodeRequest.getFirstNumberOfIdentification(), 1));
        user.setHashedCI(hashedCI);
        user.setHashedDI(hashedDI);
        user.setStatus(Status.PROCESSING);

        return userService.saveUser(user);
    }

    private String getTrimmedUserName(VerifyAuthCodeRequest verifyAuthCodeRequest) {
        return Nationality.of(verifyAuthCodeRequest.getFirstNumberOfIdentification()).getTrimmedName(verifyAuthCodeRequest.getName());
    }

    private OkCertVerifyRequest toRequestDto(VerifyAuthCodeRequest verifyAuthCodeRequest) {
        return kcbOkCertRequestConverter.convertVerifyAuthRequest(verifyAuthCodeRequest);
    }

    private OkCertSendRequest toRequestDto(SendAuthRequest sendAuthRequest) {
        return kcbOkCertRequestConverter.convertSendAuthRequest(sendAuthRequest);
    }

    private OkCertResendRequest toRequestDto(ResendAuthRequest resendAuthRequest) {
        return kcbOkCertRequestConverter.convertResendAuthRequest(resendAuthRequest);
    }

    private ag.act.model.SendAuthResponse toResponse(OkCertSendResponse responseDto) {
        return kcbOkCertResponseConverter.convertSendAuthResponse(responseDto);
    }

    private ag.act.model.ResendAuthResponse toResponse(OkCertResendResponse responseDto) {
        return kcbOkCertResponseConverter.convertResendAuthResponse(responseDto);
    }
}
