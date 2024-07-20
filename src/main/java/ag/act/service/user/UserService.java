package ag.act.service.user;

import ag.act.converter.RequestValueConverter;
import ag.act.converter.user.SimpleUserProfileDtoForDetailsConverter;
import ag.act.core.infra.S3Environment;
import ag.act.dto.SimpleUserDto;
import ag.act.dto.user.SimpleUserProfileDto;
import ag.act.dto.user.UserSearchFilterDto;
import ag.act.entity.AutomatedAuthorPush;
import ag.act.entity.FileContent;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.enums.AutomatedPushContentType;
import ag.act.enums.push.UserPushAgreementGroupType;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.model.Gender;
import ag.act.model.RegisterUserInfoRequest;
import ag.act.model.Status;
import ag.act.model.UpdateMyAddressRequest;
import ag.act.model.UpdateMyProfileRequest;
import ag.act.repository.UserRepository;
import ag.act.service.NicknameHistoryService;
import ag.act.service.io.FileContentService;
import ag.act.service.solidarity.SolidarityService;
import ag.act.util.ImageUtil;
import ag.act.util.StatusUtil;
import ag.act.validator.user.NicknameValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final S3Environment s3Environment;
    private final UserRepository userRepository;
    private final FileContentService fileContentService;
    private final NicknameHistoryService nicknameHistoryService;
    private final PasswordEncoder passwordEncoder;
    private final RequestValueConverter requestValueConverter;
    private final SolidarityService solidarityService;
    private final SimpleUserProfileDtoForDetailsConverter simpleUserProfileDtoForDetailsConverter;
    private final UserHoldingStockService userHoldingStockService;
    private final NicknameValidator nicknameValidator;
    private final UserVerificationHistoryService userVerificationHistoryService;
    private final UserBadgeVisibilityService userBadgeVisibilityService;
    private final UserSearchService userSearchService;

    public Optional<User> findUser(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByHashedCI(String hashedCI) {
        return userRepository.findByHashedCI(hashedCI);
    }

    public User saveUser(User user) {
        user.setEditedAt(LocalDateTime.now());
        return userRepository.saveAndFlush(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Optional<User> getUserByNickname(String nickname) {
        return userRepository.findUserByNickname(nickname);
    }

    public User invalidatePinNumberVerificationTime(User user) {
        user.setLastPinNumberVerifiedAt(null);
        return saveUser(user);
    }

    public User resetPinNumber(User user) {
        user.setHashedPinNumber(null);
        user.setLastPinNumberVerifiedAt(null);
        return saveUser(user);
    }

    public User updateMyProfile(User user, UpdateMyProfileRequest updateMyProfileRequest) {
        user.setJobTitle(requestValueConverter.convert(updateMyProfileRequest.getJobTitle()));
        user.setMySpeech(requestValueConverter.convert(updateMyProfileRequest.getMySpeech()));
        user.setAddress(requestValueConverter.convert(updateMyProfileRequest.getAddress()));
        user.setAddressDetail(requestValueConverter.convert(updateMyProfileRequest.getAddressDetail()));
        user.setZipcode(requestValueConverter.convert(updateMyProfileRequest.getZipcode()));
        user.setIsAgreeToReceiveMail(updateMyProfileRequest.getIsAgreeToReceiveMail());

        userBadgeVisibilityService.create(user.getId(), updateMyProfileRequest);
        return saveUser(user);
    }

    public User updateMyAddress(User user, UpdateMyAddressRequest updateMyAddressRequest) {
        user.setAddress(requestValueConverter.convert(updateMyAddressRequest.getAddress()));
        user.setAddressDetail(requestValueConverter.convert(updateMyAddressRequest.getAddressDetail()));
        user.setZipcode(requestValueConverter.convert(updateMyAddressRequest.getZipcode()));

        return saveUser(user);
    }

    public User registerUserInfo(User user, RegisterUserInfoRequest registerUserInfoRequest) {

        validateUniqueEmail(user.getId(), registerUserInfoRequest.getEmail());
        validateUniqueNickname(user.getId(), registerUserInfoRequest.getNickname());

        user.setEmail(registerUserInfoRequest.getEmail());
        user.setNickname(registerUserInfoRequest.getNickname().trim());
        user.setIsAgreeToReceiveMail(registerUserInfoRequest.getIsAgreeToReceiveMail());
        user.setStatus(ag.act.model.Status.ACTIVE);

        if (StringUtils.isBlank(user.getProfileImageUrl())) {
            user.setProfileImageUrl(pickRandomDefaultProfileImageUrl(user.getGender()));
        }

        final User savedUser = saveUser(user);

        userVerificationHistoryService.create(user.getId(), VerificationType.USER, VerificationOperationType.REGISTER);

        if (savedUser != null) {
            nicknameValidator.validateNickname(savedUser);
            nicknameValidator.validateNicknameWithin90Days(savedUser);
            nicknameHistoryService.create(savedUser);
        }
        return savedUser;
    }

    public void withdrawRequest(User user) {
        user.setStatus(Status.WITHDRAWAL_REQUESTED);
        user.setDeletedAt(LocalDateTime.now());
        resetPinNumber(user);
    }

    public User withdrawUser(User user, Status status) {
        final String hashedValue = passwordEncoder.encode(user.getId().toString());

        user.setName("탈퇴한 회원");
        user.setEmail(null);
        user.setMySpeech("");
        user.setHashedPhoneNumber(hashedValue);
        user.setBirthDate(LocalDateTime.now());
        user.setHashedCI(hashedValue);
        user.setHashedDI(hashedValue);
        user.setHashedPinNumber(hashedValue);
        user.setLastPinNumberVerifiedAt(null);
        user.setNickname("탈퇴한 회원");
        user.setJobTitle("탈퇴한 회원");
        user.setAddress("탈퇴한 회원");
        user.setAddressDetail("탈퇴한 회원");
        user.setZipcode("00000");
        user.setTotalAssetAmount(0L);
        user.setProfileImageUrl("");
        user.setPushToken(null);
        user.setStatus(status);
        user.setDeletedAt(LocalDateTime.now());
        user.setNicknameHistory(null);
        return saveUser(user);
    }

    public List<User> getWithdrawRequestedUsers() {
        return userRepository.findAllByStatusIn(List.of(Status.WITHDRAWAL_REQUESTED));
    }

    public void validateUniqueEmail(Long userId, String email) {
        final String trimmedEmail = email.trim();
        final Optional<User> userOptional = getUserByEmail(trimmedEmail);

        if (userOptional.isEmpty()) {
            return;
        }

        if (!Objects.equals(userOptional.get().getId(), userId)) {
            log.error("The email is already in use. email: {}", trimmedEmail);
            throw new BadRequestException("이미 사용중인 이메일입니다.");
        }
    }

    public void validateUniqueNickname(Long userId, String nickname) {
        final String trimmedNickname = nickname.trim();
        final Optional<User> userOptional = getUserByNickname(trimmedNickname);

        if (userOptional.isEmpty()) {
            return;
        }

        if (!Objects.equals(userOptional.get().getId(), userId)) {
            log.error("The nickname is already in use. nickname: {}", trimmedNickname);
            throw new BadRequestException("이미 사용중인 닉네임입니다.");
        }
    }

    public Map<String, UserHoldingStock> getActiveUserHoldingStocksMap(Long userId) {
        final User user = getUser(userId);
        return user.getUserHoldingStocks().stream()
            .filter(StatusUtil::isActive)
            .collect(Collectors.toMap(UserHoldingStock::getStockCode, Function.identity(), (v1, v2) -> v1));
    }

    public Map<String, UserHoldingStock> getAllUserHoldingStocksMapIncludingTestStocks(Long userId) {
        final User user = getUser(userId);
        return user.getUserHoldingStocks().stream()
            .collect(Collectors.toMap(UserHoldingStock::getStockCode, Function.identity(), (v1, v2) -> v1));
    }

    private String pickRandomDefaultProfileImageUrl(Gender gender) {
        final FileContent fileContent = fileContentService.getPickOneDefaultProfileImage(gender);

        return ImageUtil.getFullPath(s3Environment.getBaseUrl(), fileContent.getFilename());
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
    }

    public User getUserForSolidarityLeader(String stockCode) {
        Solidarity solidarity = solidarityService.findSolidarity(stockCode)
            .orElseThrow(() -> new NotFoundException("연대 정보를 확인하세요."));

        SolidarityLeader solidarityLeader = solidarity.getSolidarityLeader();
        if (solidarityLeader == null) {
            throw new NotFoundException("주주대표 정보를 확인하세요.");
        }

        return findUser(solidarityLeader.getUserId())
            .orElseThrow(() -> new NotFoundException("주주대표 정보를 확인하세요."));
    }

    public List<String> getPushTokens(String stockCode) {
        return userRepository.findAllPushTokensByStockCode(
                stockCode,
                UserPushAgreementGroupType.CMS.getAgreementTypes()
            )
            .stream()
            .filter(StringUtils::isNotBlank)
            .toList();
    }

    public SimpleUserProfileDto getSimpleUserProfileDto(User user, String stockCode) {
        final List<UserHoldingStock> userHoldingStocks = Optional.ofNullable(user.getUserHoldingStocks()).orElse(List.of());
        final UserHoldingStock queriedUserHoldingStock = userHoldingStockService.findQueriedUserHoldingStock(stockCode, userHoldingStocks);

        return simpleUserProfileDtoForDetailsConverter.convert(user, queriedUserHoldingStock, userHoldingStocks);
    }

    public Set<String> getPushTokensByStockGroupId(Long stockGroupId) {
        return userRepository.findAllPushTokensByStockGroup(
                stockGroupId,
                UserPushAgreementGroupType.CMS.getAgreementTypes()
            )
            .stream()
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toSet());
    }

    public Optional<User> getActiveUserByNameAndBirthDate(String name, LocalDateTime birthDate) {
        return userRepository.findByNameAndBirthDateAndStatus(name, birthDate, Status.ACTIVE);
    }

    public Page<User> getUserList(UserSearchFilterDto userSearchFilterDto, Pageable pageable) {
        return userSearchService.getUserList(userSearchFilterDto, pageable);
    }

    public Page<User> findAllVotedInPoll(Long pollId, Pageable pageable) {
        return userRepository.findAllVotedInPoll(pollId, pageable);
    }

    public List<String> findAllTokens(Long pushId, AutomatedAuthorPush automatedAuthorPush) {
        if (automatedAuthorPush.getContentType() == AutomatedPushContentType.COMMENT) {
            return findAllTokensForCommentAuthor(pushId, automatedAuthorPush.getId());
        }

        return findAllTokensForPostAuthor(pushId, automatedAuthorPush.getId());
    }

    private List<String> findAllTokensForPostAuthor(Long pushId, Long automatedAuthorPushId) {
        return userRepository.findAllPushTokensForPostAuthor(
                pushId,
                automatedAuthorPushId,
                UserPushAgreementGroupType.AUTHOR.getAgreementTypes()
            )
            .stream()
            .filter(StringUtils::isNotBlank)
            .toList();
    }

    private List<String> findAllTokensForCommentAuthor(Long pushId, Long automatedAuthorPushId) {
        return userRepository.findAllPushTokensForCommentAuthor(
                pushId,
                automatedAuthorPushId,
                UserPushAgreementGroupType.AUTHOR.getAgreementTypes()
            )
            .stream()
            .filter(StringUtils::isNotBlank)
            .toList();
    }

    public Optional<User> getAcceptorByUserIdAndStockCode(Long userId, String stockCode) {
        return userRepository.findAcceptorByUserIdAndStockCode(userId, stockCode);
    }

    public Optional<User> getAcceptorByStockCode(String stockCode) {
        return userRepository.findAcceptorByStockCode(stockCode);
    }

    public Page<SimpleUserDto> findAllSimpleUsers(Pageable pageable) {
        return userRepository.findActiveUsers(pageable);
    }

    public List<SimpleUserDto> findAllSimpleUsersInAllStatus(List<Long> userIds) {
        return userRepository.findAllSimpleUsersInAllStatus(userIds);
    }

    public Optional<User> findUserByApplicantId(Long applicantId) {
        return userRepository.findUserByApplicantId(applicantId);
    }
}
