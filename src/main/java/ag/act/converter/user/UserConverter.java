package ag.act.converter.user;

import ag.act.converter.Converter;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.DecryptColumnConverter;
import ag.act.converter.stock.SimpleStockResponseConverter;
import ag.act.entity.Role;
import ag.act.entity.User;
import ag.act.enums.RoleType;
import ag.act.model.SimpleStockResponse;
import ag.act.model.UserBadgeVisibilityResponse;
import ag.act.model.UserResponse;
import ag.act.service.user.UserBadgeVisibilityService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.service.user.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserConverter implements Converter<User, UserResponse> {

    private final UserRoleService userRoleService;
    private final UserHoldingStockService userHoldingStockService;
    private final DecryptColumnConverter decryptColumnConverter;
    private final SimpleStockResponseConverter simpleStockResponseConverter;
    private final UserBadgeVisibilityService userBadgeVisibilityService;
    private final UserBadgeVisibilityResponseConverter userBadgeVisibilityResponseConverter;

    public UserResponse convert(User user) {

        final List<Role> roles = userRoleService.getActiveRoles(user.getId());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setBirthDate(DateTimeConverter.convert(user.getBirthDate()));
        userResponse.setGender(user.getGender());
        userResponse.setIsAgreeToReceiveMail(user.getIsAgreeToReceiveMail());
        userResponse.setMySpeech(user.getMySpeech());
        userResponse.setNickname(user.getNickname());
        userResponse.setJobTitle(user.getJobTitle());
        userResponse.setAddress(user.getAddress());
        userResponse.setAddressDetail(user.getAddressDetail());
        userResponse.setZipcode(user.getZipcode());
        userResponse.setTotalAssetAmount(userHoldingStockService.getTotalAssetAmount(user.getId()));
        userResponse.setProfileImageUrl(user.getProfileImageUrl());
        userResponse.setStatus(user.getStatus());
        userResponse.setAuthType(user.getAuthType());
        userResponse.setLastPinNumberVerifiedAt(DateTimeConverter.convert(user.getLastPinNumberVerifiedAt()));
        userResponse.setCreatedAt(DateTimeConverter.convert(user.getCreatedAt()));
        userResponse.setUpdatedAt(DateTimeConverter.convert(user.getUpdatedAt()));
        userResponse.setDeletedAt(DateTimeConverter.convert(user.getDeletedAt()));
        userResponse.setEditedAt(DateTimeConverter.convert(user.getEditedAt()));
        userResponse.setIsPinNumberRegistered(user.getHashedPinNumber() != null);
        userResponse.setIsChangePasswordRequired(user.getIsChangePasswordRequired());
        userResponse.setIsAdmin(isAdmin(roles));
        userResponse.setRoles(getRoles(roles));
        userResponse.setPhoneNumber(decryptColumnConverter.convert(user.getHashedPhoneNumber()));
        userResponse.setLeadingSolidarityStockCodes(getLeadingSolidarityStockCodes(user));
        userResponse.setUserBadgeVisibilities(getUserBadgeVisibilities(user));
        userResponse.lastNicknameUpdatedAt(getLastNicknameUpdatedAt(user).orElse(null));
        userResponse.setHoldingStocks(getHoldingStockSimpleResponseList(user.getId()));
        userResponse.setIsSolidarityLeaderConfidentialAgreementSigned(user.getIsSolidarityLeaderConfidentialAgreementSigned());

        return userResponse;
    }

    private Optional<Instant> getLastNicknameUpdatedAt(User user) {
        if (user.getNicknameHistory() != null && !user.getNicknameHistory().getIsFirst()) {
            return Optional.ofNullable(DateTimeConverter.convert(user.getNicknameHistory().getUpdatedAt()));
        }
        return Optional.empty();
    }

    @NotNull
    private List<UserBadgeVisibilityResponse> getUserBadgeVisibilities(User user) {
        return userBadgeVisibilityResponseConverter.convert(userBadgeVisibilityService.getUserBadgeVisibilities(user.getId()));
    }

    private List<SimpleStockResponse> getHoldingStockSimpleResponseList(Long userId) {
        return simpleStockResponseConverter.convertSimpleStocks(
            userHoldingStockService.findAllSimpleStocksByUserId(userId));
    }

    private List<String> getLeadingSolidarityStockCodes(User user) {
        return userHoldingStockService.getLeadingSolidarityStockCodes(user.getId());
    }

    private Boolean isAdmin(List<Role> roles) {
        return roles
            .stream()
            .map(Role::getType)
            .anyMatch(RoleType::isAdminRoleType);
    }

    private List<String> getRoles(List<Role> roles) {
        return roles
            .stream()
            .map(Role::getType)
            .map(RoleType::name)
            .toList();
    }

    @Override
    public UserResponse apply(User user) {
        return convert(user);
    }
}
