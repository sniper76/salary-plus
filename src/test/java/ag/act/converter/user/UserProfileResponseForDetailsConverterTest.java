package ag.act.converter.user;

import ag.act.converter.ContentUserProfileAdminLabelsHider;
import ag.act.converter.ContentUserProfileAnonymousFieldsSetter;
import ag.act.converter.stock.SimpleStockResponseConverter;
import ag.act.entity.ContentUserProfile;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willDoNothing;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
public class UserProfileResponseForDetailsConverterTest {
    private static final String ANONYMOUS_NICKNAME = "익명";
    @InjectMocks
    private UserProfileResponseForDetailsConverter converter;
    @Mock
    private UserIpConverter userIpConverter;
    @Mock
    private SolidarityLeaderService solidarityLeaderService;
    @Mock
    private SimpleStockResponseConverter simpleStockResponseConverter;
    @Mock
    private ContentUserProfileAnonymousFieldsSetter contentUserProfileAnonymousFieldsSetter;
    @Mock
    private ContentUserProfileAdminLabelsHider contentUserProfileAdminLabelsHider;

    @Nested
    class ConvertGivenContentUserProfile {
        private Long userId;
        private String userIp;
        @Mock
        private ContentUserProfile contentUserProfile;

        @BeforeEach
        void setUp() {
            userId = someLong();
            userIp = someString(10);

            given(solidarityLeaderService.findAllLeadingSimpleStocks(userId)).willReturn(List.of());
            given(simpleStockResponseConverter.convertSimpleStocks(anyList())).willReturn(List.of());
            given(contentUserProfile.getNickname()).willReturn(someString(10));
            given(contentUserProfile.getIndividualStockCountLabel()).willReturn(someString(10));
            given(contentUserProfile.getTotalAssetLabel()).willReturn(someString(10));
            given(contentUserProfile.getProfileImageUrl()).willReturn(someString(10));
            given(contentUserProfile.getUserIp()).willReturn(userIp);
            given(userIpConverter.convert(userIp)).willReturn(userIp);
            mockContentUserProfileAnonymousFieldsSetterForAnonymous();
        }

        @Nested
        class WhenUserAdmin {
            @Test
            void shouldConvertWithNullLabels() {
                // Given
                mockContentUserProfileAdminLabelsHiderForAdmin();

                // When
                ag.act.model.UserProfileResponse userProfileResponse = converter.convert(userId, contentUserProfile);

                // Then
                assertResponseForAdmin(userProfileResponse);

            }
        }

        @Nested
        class WhenUserAnonymous {
            @Test
            void shouldConvertWithNullLabels() {
                // Given
                mockContentUserProfileAdminLabelsHiderForNormalUser();
                given(contentUserProfile.getNickname()).willReturn(ANONYMOUS_NICKNAME);

                // When
                ag.act.model.UserProfileResponse userProfileResponse = converter.convert(userId, contentUserProfile);

                // Then
                assertResponseForAnonymous(userProfileResponse);
            }
        }

        @Nested
        class WhenNormalUser {
            @Test
            void shouldConvert() {
                // Given
                mockContentUserProfileAdminLabelsHiderForNormalUser();

                // When
                ag.act.model.UserProfileResponse userProfileResponse = converter.convert(userId, contentUserProfile);

                // Then
                assertResponse(userProfileResponse);
            }
        }

        private void assertResponseForAdmin(ag.act.model.UserProfileResponse userProfileResponse) {
            assertThat(userProfileResponse.getNickname(), is(contentUserProfile.getNickname()));
            assertThat(userProfileResponse.getIndividualStockCountLabel(), nullValue());
            assertThat(userProfileResponse.getTotalAssetLabel(), nullValue());
            assertThat(userProfileResponse.getProfileImageUrl(), is(contentUserProfile.getProfileImageUrl()));
            assertThat(userProfileResponse.getUserIp(), is(userIp));
        }

        private void assertResponseForAnonymous(ag.act.model.UserProfileResponse userProfileResponse) {
            assertThat(userProfileResponse.getNickname(), is(contentUserProfile.getNickname()));
            assertThat(userProfileResponse.getIndividualStockCountLabel(), nullValue());
            assertThat(userProfileResponse.getTotalAssetLabel(), nullValue());
            assertThat(userProfileResponse.getProfileImageUrl(), nullValue());
            assertThat(userProfileResponse.getUserIp(), is(userIp));
        }

        private void assertResponse(ag.act.model.UserProfileResponse userProfileResponse) {
            assertThat(userProfileResponse.getNickname(), is(contentUserProfile.getNickname()));
            assertThat(
                userProfileResponse.getIndividualStockCountLabel(),
                is(contentUserProfile.getIndividualStockCountLabel())
            );
            assertThat(userProfileResponse.getTotalAssetLabel(), is(contentUserProfile.getTotalAssetLabel()));
            assertThat(userProfileResponse.getProfileImageUrl(), is(contentUserProfile.getProfileImageUrl()));
            assertThat(userProfileResponse.getUserIp(), is(userIp));
        }
    }

    private void mockContentUserProfileAnonymousFieldsSetterForAnonymous() {
        final Answer<Void> setFieldsForAnonymous = invocation -> {
            final ContentUserProfile contentUserProfile1 = invocation.getArgument(0, ContentUserProfile.class);
            contentUserProfile1.setNickname(contentUserProfile1.getNickname());
            contentUserProfile1.setIndividualStockCountLabel(null);
            contentUserProfile1.setTotalAssetLabel(null);
            contentUserProfile1.setProfileImageUrl(null);
            contentUserProfile1.setIsSolidarityLeader(false);
            return null;
        };

        willAnswer(setFieldsForAnonymous).given(contentUserProfileAnonymousFieldsSetter).setFieldsForAnonymous(any(), anyString());
        willAnswer(setFieldsForAnonymous).given(contentUserProfileAnonymousFieldsSetter).setFieldsForAnonymous(any());
    }

    private void mockContentUserProfileAdminLabelsHiderForNormalUser() {
        willDoNothing().given(contentUserProfileAdminLabelsHider).hideAdminLabels(anyLong(), any());
    }

    private void mockContentUserProfileAdminLabelsHiderForAdmin() {
        willAnswer(invocation -> {
            ContentUserProfile contentUserProfile = invocation.getArgument(1);
            contentUserProfile.setIndividualStockCountLabel(null);
            contentUserProfile.setTotalAssetLabel(null);
            return null;
        }).given(contentUserProfileAdminLabelsHider).hideAdminLabels(anyLong(), any());
    }
}
