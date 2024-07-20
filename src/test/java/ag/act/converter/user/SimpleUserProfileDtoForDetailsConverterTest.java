package ag.act.converter.user;

import ag.act.converter.ContentUserProfileAdminLabelsHider;
import ag.act.dto.user.SimpleUserProfileDto;
import ag.act.entity.ContentUserProfile;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.util.badge.StockCountLabelGenerator;
import ag.act.util.badge.TotalAssetLabelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willDoNothing;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
public class SimpleUserProfileDtoForDetailsConverterTest {
    @InjectMocks
    private SimpleUserProfileDtoForDetailsConverter converter;
    @Mock
    private TotalAssetLabelGenerator totalAssetLabelGenerator;
    @Mock
    private StockCountLabelGenerator stockCountLabelGenerator;
    @Mock
    private ContentUserProfileAdminLabelsHider contentUserProfileAdminLabelsHider;

    @Nested
    class ConvertGivenUser {
        @Mock
        private User user;
        private String individualStockCountLabel;
        private String totalAssetLabel;

        @BeforeEach
        void setUp() {
            individualStockCountLabel = someString(10);
            totalAssetLabel = someString(10);
            given(user.getId()).willReturn(someLong());
            given(user.getNickname()).willReturn(someString(10));
            given(user.getProfileImageUrl()).willReturn(someString(10));
        }

        @Nested
        class AndGivenLabels {
            @Nested
            class WhenUserAdmin {
                @Test
                void shouldConvertWithNullLabels() {
                    // Given
                    mockContentUserProfileAdminLabelsHiderForAdmin();

                    // When
                    SimpleUserProfileDto simpleUserProfileDto = converter.convert(user, individualStockCountLabel, totalAssetLabel);

                    // Then
                    assertResponseForAdmin(simpleUserProfileDto);
                }
            }

            @Nested
            class WhenNormalUser {
                @Test
                void shouldConvert() {
                    // Given
                    mockContentUserProfileAdminLabelsHiderForNormalUser();

                    // When
                    SimpleUserProfileDto simpleUserProfileDto = converter.convert(user, individualStockCountLabel, totalAssetLabel);

                    // Then
                    assertResponse(simpleUserProfileDto);
                }
            }
        }

        @Nested
        class AndGivenUserHoldingStocks {
            @Mock
            private UserHoldingStock queriedUserHoldingStock;
            @Mock
            private List<UserHoldingStock> userHoldingStocks;

            @BeforeEach
            void setUp() {
                given(stockCountLabelGenerator.generate(queriedUserHoldingStock)).willReturn(individualStockCountLabel);
                given(totalAssetLabelGenerator.generate(userHoldingStocks)).willReturn(totalAssetLabel);
            }

            @Nested
            class WhenUserAdmin {
                @Test
                void shouldConvertWithNullLabels() {
                    // Given
                    mockContentUserProfileAdminLabelsHiderForAdmin();

                    // When
                    SimpleUserProfileDto simpleUserProfileDto = converter.convert(user, queriedUserHoldingStock, userHoldingStocks);

                    // Then
                    assertResponseForAdmin(simpleUserProfileDto);
                }
            }

            @Nested
            class WhenNormalUser {
                @Test
                void shouldConvert() {
                    // Given
                    mockContentUserProfileAdminLabelsHiderForNormalUser();

                    // When
                    SimpleUserProfileDto simpleUserProfileDto = converter.convert(user, queriedUserHoldingStock, userHoldingStocks);

                    // Then
                    assertResponse(simpleUserProfileDto);
                }
            }
        }

        private void assertResponse(SimpleUserProfileDto userProfileResponse) {
            assertThat(userProfileResponse.getNickname(), is(user.getNickname()));
            assertThat(userProfileResponse.getProfileImageUrl(), is(user.getProfileImageUrl()));
            assertThat(userProfileResponse.getTotalAssetLabel(), is(totalAssetLabel));
            assertThat(userProfileResponse.getIndividualStockCountLabel(), is(individualStockCountLabel));
        }

        private void assertResponseForAdmin(SimpleUserProfileDto userProfileResponse) {
            assertThat(userProfileResponse.getNickname(), is(user.getNickname()));
            assertThat(userProfileResponse.getProfileImageUrl(), is(user.getProfileImageUrl()));
            assertThat(userProfileResponse.getTotalAssetLabel(), nullValue());
            assertThat(userProfileResponse.getIndividualStockCountLabel(), nullValue());
        }
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
