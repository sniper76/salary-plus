package ag.act.converter;

import ag.act.TestUtil;
import ag.act.converter.stock.SimpleStockResponseConverter;
import ag.act.converter.user.UserBadgeVisibilityResponseConverter;
import ag.act.converter.user.UserConverter;
import ag.act.entity.NicknameHistory;
import ag.act.entity.Role;
import ag.act.entity.User;
import ag.act.entity.UserBadgeVisibility;
import ag.act.enums.RoleType;
import ag.act.model.SimpleStockResponse;
import ag.act.model.UserBadgeVisibilityResponse;
import ag.act.model.UserResponse;
import ag.act.repository.interfaces.SimpleStock;
import ag.act.service.user.UserBadgeVisibilityService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.service.user.UserRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static ag.act.TestUtil.someLocalDateTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserConverterTest {

    @InjectMocks
    private UserConverter converter;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private UserHoldingStockService userHoldingStockService;
    @Mock
    private UserBadgeVisibilityService userBadgeVisibilityService;
    @Mock
    private DecryptColumnConverter decryptColumnConverter;
    @Mock
    private UserBadgeVisibilityResponseConverter userBadgeVisibilityResponseConverter;
    @Mock
    private User user;
    @Mock
    private NicknameHistory nicknameHistory;
    @Mock
    private List<String> leadingSolidarityStockCodes;
    private UserResponse userResponse;
    private Boolean isAdmin;
    private String phoneNumber;
    @Mock
    private Role role;
    @Mock
    private Role adminRole;
    private RoleType roleType;
    private Long totalAssetAmount;
    private List<UserBadgeVisibilityResponse> userBadgeVisibilityResponses;
    @Mock
    private List<SimpleStock> simpleStockDtoList;
    @Mock
    private List<SimpleStockResponse> simpleStockResponseList;
    @Mock
    private SimpleStockResponseConverter simpleStockResponseConverter;

    @BeforeEach
    void setUp() {

        final String hashedPhoneNumber = someString(10);
        final Long userId = someLong();
        isAdmin = true;
        phoneNumber = someString(20);
        roleType = someEnum(RoleType.class);
        totalAssetAmount = someLong();

        setUserBadgeVisibility(userId);

        given(decryptColumnConverter.convert(hashedPhoneNumber)).willReturn(phoneNumber);
        given(userHoldingStockService.getLeadingSolidarityStockCodes(userId)).willReturn(leadingSolidarityStockCodes);
        given(userRoleService.getActiveRoles(userId)).willReturn(List.of(role, adminRole));
        given(role.getType()).willReturn(roleType);
        given(adminRole.getType()).willReturn(RoleType.ADMIN);

        given(user.getId()).willReturn(userId);
        given(user.getName()).willReturn(someString(5));
        given(user.getEmail()).willReturn(someString(5));
        given(user.getBirthDate()).willReturn(TestUtil.someBirthDay());
        given(user.getGender()).willReturn(someEnum(ag.act.model.Gender.class));
        given(user.getIsAgreeToReceiveMail()).willReturn(someBoolean());
        given(user.getMySpeech()).willReturn(someString(5));
        given(user.getNickname()).willReturn(someString(5));
        given(user.getJobTitle()).willReturn(someString(5));
        given(user.getAddress()).willReturn(someString(5));
        given(user.getAddressDetail()).willReturn(someString(5));
        given(user.getZipcode()).willReturn(someString(5));
        given(user.getTotalAssetAmount()).willReturn(someLong());
        given(user.getProfileImageUrl()).willReturn(someString(5));
        given(user.getStatus()).willReturn(someEnum(ag.act.model.Status.class));
        given(user.getAuthType()).willReturn(someEnum(ag.act.model.AuthType.class));
        given(user.getLastPinNumberVerifiedAt()).willReturn(someLocalDateTime());
        given(user.getCreatedAt()).willReturn(someLocalDateTime());
        given(user.getUpdatedAt()).willReturn(someLocalDateTime());
        given(user.getDeletedAt()).willReturn(someLocalDateTime());
        given(user.getEditedAt()).willReturn(someLocalDateTime());
        given(user.getHashedPhoneNumber()).willReturn(hashedPhoneNumber);
        given(userHoldingStockService.findAllSimpleStocksByUserId(userId)).willReturn(simpleStockDtoList);
        given(simpleStockResponseConverter.convertSimpleStocks(simpleStockDtoList)).willReturn(simpleStockResponseList);
    }

    private void setUserBadgeVisibility(Long userId) {
        UserBadgeVisibility userBadgeVisibility = new UserBadgeVisibility();
        List<UserBadgeVisibility> userBadgeVisibilities = List.of(
            userBadgeVisibility,
            userBadgeVisibility
        );

        UserBadgeVisibilityResponse userBadgeVisibilityResponse = new UserBadgeVisibilityResponse();
        userBadgeVisibilityResponses = List.of(
            userBadgeVisibilityResponse,
            userBadgeVisibilityResponse
        );

        given(userBadgeVisibilityResponseConverter.convert(userBadgeVisibilities)).willReturn(userBadgeVisibilityResponses);
        given(userBadgeVisibilityService.getUserBadgeVisibilities(userId)).willReturn(userBadgeVisibilities);
    }

    @Nested
    class WhenNicknameHistoryIsFirst extends DefaultTestCases {
        @BeforeEach
        void setUp() {
            given(user.getNicknameHistory()).willReturn(nicknameHistory);
            given(nicknameHistory.getIsFirst()).willReturn(true);
            given(userHoldingStockService.getTotalAssetAmount(user.getId())).willReturn(totalAssetAmount);
            userResponse = converter.convert(user);
        }

        @Test
        void shouldLastNicknameUpdatedAtNull() {
            assertThat(userResponse.getLastNicknameUpdatedAt(), is(nullValue()));
        }
    }

    @Nested
    class WhenNicknameHistoryIsNotFirst extends DefaultTestCases {
        @BeforeEach
        void setUp() {

            given(user.getNicknameHistory()).willReturn(nicknameHistory);
            given(nicknameHistory.getIsFirst()).willReturn(false);
            given(nicknameHistory.getUpdatedAt()).willReturn(someLocalDateTime());
            given(userHoldingStockService.getTotalAssetAmount(user.getId())).willReturn(totalAssetAmount);
            userResponse = converter.convert(user);
        }

        @Test
        void shouldReturnLastNicknameUpdatedAt() {
            assertThat(userResponse.getLastNicknameUpdatedAt(), is(DateTimeConverter.convert(nicknameHistory.getUpdatedAt())));

        }
    }

    @SuppressWarnings("unused")
    class DefaultTestCases {
        @Test
        void shouldReturnTheSameUserInfo() {
            assertThat(userResponse.getId(), is(user.getId()));
            assertThat(userResponse.getName(), is(user.getName()));
            assertThat(userResponse.getEmail(), is(user.getEmail()));
            assertThat(userResponse.getBirthDate(), is(DateTimeConverter.convert(user.getBirthDate())));
            assertThat(userResponse.getGender(), is(user.getGender()));
            assertThat(userResponse.getIsAgreeToReceiveMail(), is(user.getIsAgreeToReceiveMail()));
            assertThat(userResponse.getMySpeech(), is(user.getMySpeech()));
            assertThat(userResponse.getNickname(), is(user.getNickname()));
            assertThat(userResponse.getJobTitle(), is(user.getJobTitle()));
            assertThat(userResponse.getAddress(), is(user.getAddress()));
            assertThat(userResponse.getAddressDetail(), is(user.getAddressDetail()));
            assertThat(userResponse.getZipcode(), is(user.getZipcode()));
            assertThat(userResponse.getTotalAssetAmount(), is(totalAssetAmount));
            assertThat(userResponse.getProfileImageUrl(), is(user.getProfileImageUrl()));
            assertThat(userResponse.getStatus(), is(user.getStatus()));
            assertThat(userResponse.getAuthType(), is(user.getAuthType()));
            assertThat(userResponse.getLastPinNumberVerifiedAt(), is(DateTimeConverter.convert(user.getLastPinNumberVerifiedAt())));
            assertThat(userResponse.getCreatedAt(), is(DateTimeConverter.convert(user.getCreatedAt())));
            assertThat(userResponse.getUpdatedAt(), is(DateTimeConverter.convert(user.getUpdatedAt())));
            assertThat(userResponse.getDeletedAt(), is(DateTimeConverter.convert(user.getDeletedAt())));
            assertThat(userResponse.getEditedAt(), is(DateTimeConverter.convert(user.getEditedAt())));
            assertThat(userResponse.getIsPinNumberRegistered(), is(user.getHashedPinNumber() != null));
            assertThat(userResponse.getIsAdmin(), is(isAdmin));
            assertThat(userResponse.getPhoneNumber(), is(phoneNumber));
            assertThat(userResponse.getLeadingSolidarityStockCodes(), is(leadingSolidarityStockCodes));
            assertThat(userResponse.getRoles(), is(List.of(roleType.name(), RoleType.ADMIN.name())));
            assertThat(userResponse.getUserBadgeVisibilities(), is(userBadgeVisibilityResponses));
            assertThat(userResponse.getHoldingStocks(), is(simpleStockResponseList));
        }
    }
}
