package ag.act.service.push;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.push.UserPushAgreementItemGenerator;
import ag.act.dto.push.UserPushAgreementStatusDto;
import ag.act.entity.User;
import ag.act.entity.UserPushAgreement;
import ag.act.enums.push.UserPushAgreementGroupType;
import ag.act.enums.push.UserPushAgreementType;
import ag.act.repository.UserPushAgreementRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserPushAgreementServiceTest {
    private List<MockedStatic<?>> statics;
    @InjectMocks
    private UserPushAgreementService userPushAgreementService;
    @Mock
    private UserPushAgreementItemGenerator userPushAgreementItemGenerator;
    @Mock
    private UserPushAgreementChecker userPushAgreementChecker;
    @Mock
    private UserPushAgreementRepository userPushAgreementRepository;
    @Mock
    private User user;
    private final Long userId = someLong();

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(user.getId()).willReturn(userId);
    }

    @Nested
    class WhenGetUserPushAgreements {
        private static final String CMS_GROUP_TITLE = "액트공지 / 종목알림 / 추천글";
        private static final String AUTHOR_GROUP_TITLE = "액트베스트 진입 / 새글";
        private static final String ALL_TITLE = "푸시 알림 받기(전체)";
        private static final List<String> CMS_AGREEMENT_TYPE_NAMES = List.of(
            "ACT_NOTICE",
            "STOCK_NOTICE",
            "RECOMMENDATION"
        );
        private static final List<String> AUTHOR_AGREEMENT_TYPE_NAMES = List.of(
            "ACT_BEST_ENTER",
            "NEW_COMMENT"
        );
        private static final String SUB_ITEM_TYPE_NAME = "SUB";
        private static final String ALL_ITEM_TYPE_NAME = "ALL";

        private final boolean isAgreeToReceiveCmsPush = someBoolean();
        private final boolean isAgreeToReceiveAuthorPush = someBoolean();

        @Test
        void shouldGet() {
            // Given
            given(userPushAgreementChecker.isAgreeToReceive(userId, UserPushAgreementGroupType.CMS))
                .willReturn(isAgreeToReceiveCmsPush);
            given(userPushAgreementChecker.isAgreeToReceive(userId, UserPushAgreementGroupType.AUTHOR))
                .willReturn(isAgreeToReceiveAuthorPush);

            given(userPushAgreementItemGenerator.generate(any(), any(), any(), any())).willCallRealMethod();

            // When
            List<ag.act.model.UserPushAgreementItem> items = userPushAgreementService.getUserPushAgreements().getData();

            // Then
            assertThat(items.size(), is(3));

            assertThat(items.get(0), is(new ag.act.model.UserPushAgreementItem()
                .title(ALL_TITLE)
                .agreementTypes(
                    Stream.concat(
                        CMS_AGREEMENT_TYPE_NAMES.stream(),
                        AUTHOR_AGREEMENT_TYPE_NAMES.stream()
                    ).toList()
                )
                .value(isAgreeToReceiveCmsPush | isAgreeToReceiveAuthorPush)
                .itemType(ALL_ITEM_TYPE_NAME))
            );

            assertThat(items.get(1), is(new ag.act.model.UserPushAgreementItem()
                .title(CMS_GROUP_TITLE)
                .agreementTypes(CMS_AGREEMENT_TYPE_NAMES)
                .value(isAgreeToReceiveCmsPush)
                .itemType(SUB_ITEM_TYPE_NAME))
            );

            assertThat(items.get(2), is(new ag.act.model.UserPushAgreementItem()
                .title(AUTHOR_GROUP_TITLE)
                .agreementTypes(AUTHOR_AGREEMENT_TYPE_NAMES)
                .value(isAgreeToReceiveAuthorPush)
                .itemType(SUB_ITEM_TYPE_NAME))
            );
        }
    }

    @Nested
    class WhenUpdate {
        private final List<UserPushAgreementStatusDto> userPushAgreementStatusDtoList = List.of(
            new UserPushAgreementStatusDto(UserPushAgreementType.ACT_NOTICE, true),
            new UserPushAgreementStatusDto(UserPushAgreementType.ACT_BEST_ENTER, false)
        );

        @Nested
        class WhenAlreadyExist {
            @Mock
            private UserPushAgreement actNoticeAgreement;
            @Mock
            private UserPushAgreement actBestEnterAgreement;

            @Test
            void shouldUpdate() {
                // Given
                given(userPushAgreementRepository.findByUserIdAndType(user.getId(), UserPushAgreementType.ACT_NOTICE))
                    .willReturn(Optional.of(actNoticeAgreement));
                given(userPushAgreementRepository.findByUserIdAndType(user.getId(), UserPushAgreementType.ACT_BEST_ENTER))
                    .willReturn(Optional.of(actBestEnterAgreement));

                // When
                userPushAgreementService.updateUserPushAgreementStatus(userPushAgreementStatusDtoList);

                // Then
                then(actNoticeAgreement).should().setAgreeToReceive(true);
                then(actBestEnterAgreement).should().setAgreeToReceive(false);
            }
        }

        @Nested
        class WhenNotExist {
            @Test
            void shouldCreate() {
                // Given
                given(userPushAgreementRepository.findByUserIdAndType(user.getId(), UserPushAgreementType.ACT_NOTICE))
                    .willReturn(Optional.empty());
                given(userPushAgreementRepository.findByUserIdAndType(user.getId(), UserPushAgreementType.ACT_BEST_ENTER))
                    .willReturn(Optional.empty());

                ArgumentCaptor<UserPushAgreement> captor = ArgumentCaptor.forClass(UserPushAgreement.class);

                // When
                userPushAgreementService.updateUserPushAgreementStatus(userPushAgreementStatusDtoList);

                // Then
                then(userPushAgreementRepository).should(times(2)).save(captor.capture());

                List<UserPushAgreement> capturedArguments = captor.getAllValues();
                assertThat(capturedArguments.size(), is(2));
                assertThat(capturedArguments.get(0).getType(), is(UserPushAgreementType.ACT_NOTICE));
                assertThat(capturedArguments.get(0).getAgreeToReceive(), is(true));
                assertThat(capturedArguments.get(1).getType(), is(UserPushAgreementType.ACT_BEST_ENTER));
                assertThat(capturedArguments.get(1).getAgreeToReceive(), is(false));
            }
        }
    }
}
