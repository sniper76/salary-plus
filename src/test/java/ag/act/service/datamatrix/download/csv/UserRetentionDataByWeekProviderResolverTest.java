package ag.act.service.datamatrix.download.csv;

import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.service.download.datamatrix.data.provider.DigitalDocumentParticipationRateGivenRegisterByWeekProvider;
import ag.act.service.download.datamatrix.data.provider.NonRetainedUserGivenRegisterAndDigitalDocumentCompleteByWeekProvider;
import ag.act.service.download.datamatrix.data.provider.NonRetainedUserGivenRegisterAndNoDigitalDocumentCompleteByWeekProvider;
import ag.act.service.download.datamatrix.data.provider.PinVerificationGivenFirstDigitalDocumentCompleteByWeekProvider;
import ag.act.service.download.datamatrix.data.provider.PinVerificationGivenRegisterAndDigitalDocumentCompleteByWeekProvider;
import ag.act.service.download.datamatrix.data.provider.PinVerificationGivenRegisterAndNoDigitalDocumentCompleteByWeekProvider;
import ag.act.service.download.datamatrix.data.provider.PinVerificationGivenRegisterByWeekProvider;
import ag.act.service.download.datamatrix.data.provider.UserRetentionDataByWeekProvider;
import ag.act.service.download.datamatrix.data.provider.UserRetentionDataByWeekProviderResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willCallRealMethod;

@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("LineLength")
class UserRetentionDataByWeekProviderResolverTest {
    @InjectMocks
    private UserRetentionDataByWeekProviderResolver resolver;
    @Mock
    private PinVerificationGivenRegisterByWeekProvider pinVerificationGivenRegisterByWeekGenerator;
    @Mock
    private DigitalDocumentParticipationRateGivenRegisterByWeekProvider digitalDocumentParticipationRateGivenRegisterByWeekGenerator;
    @Mock
    private PinVerificationGivenFirstDigitalDocumentCompleteByWeekProvider pinVerificationGivenFirstDigitalDocumentCompleteByWeekGenerator;
    @Mock
    private PinVerificationGivenRegisterAndDigitalDocumentCompleteByWeekProvider pinVerificationGivenRegisterAndDigitalDocumentCompleteByWeekGenerator;
    @Mock
    private PinVerificationGivenRegisterAndNoDigitalDocumentCompleteByWeekProvider pinVerificationGivenRegisterAndNoDigitalDocumentCompleteByWeekGenerator;
    @Mock
    private NonRetainedUserGivenRegisterAndDigitalDocumentCompleteByWeekProvider nonRetainedUserGivenRegisterAndDigitalDocumentCompleteByWeekGenerator;
    @Mock
    private NonRetainedUserGivenRegisterAndNoDigitalDocumentCompleteByWeekProvider nonRetainedUserGivenRegisterAndNoDigitalDocumentCompleteByWeekGenerator;

    @BeforeEach
    void setUp() {

        willCallRealMethod().given(pinVerificationGivenRegisterByWeekGenerator).supports(any(UserRetentionWeeklyCsvDataType.class));
        willCallRealMethod().given(pinVerificationGivenFirstDigitalDocumentCompleteByWeekGenerator).supports(any(UserRetentionWeeklyCsvDataType.class));
        willCallRealMethod().given(digitalDocumentParticipationRateGivenRegisterByWeekGenerator).supports(any(UserRetentionWeeklyCsvDataType.class));
        willCallRealMethod().given(pinVerificationGivenRegisterAndDigitalDocumentCompleteByWeekGenerator).supports(any(UserRetentionWeeklyCsvDataType.class));
        willCallRealMethod().given(pinVerificationGivenRegisterAndNoDigitalDocumentCompleteByWeekGenerator).supports(any(UserRetentionWeeklyCsvDataType.class));
        willCallRealMethod().given(nonRetainedUserGivenRegisterAndDigitalDocumentCompleteByWeekGenerator).supports(any(UserRetentionWeeklyCsvDataType.class));
        willCallRealMethod().given(nonRetainedUserGivenRegisterAndNoDigitalDocumentCompleteByWeekGenerator).supports(any(UserRetentionWeeklyCsvDataType.class));

        resolver = new UserRetentionDataByWeekProviderResolver(
            List.of(
                pinVerificationGivenRegisterByWeekGenerator,
                digitalDocumentParticipationRateGivenRegisterByWeekGenerator,
                pinVerificationGivenFirstDigitalDocumentCompleteByWeekGenerator,
                pinVerificationGivenRegisterAndDigitalDocumentCompleteByWeekGenerator,
                pinVerificationGivenRegisterAndNoDigitalDocumentCompleteByWeekGenerator,
                nonRetainedUserGivenRegisterAndDigitalDocumentCompleteByWeekGenerator,
                nonRetainedUserGivenRegisterAndNoDigitalDocumentCompleteByWeekGenerator
            ),
            pinVerificationGivenRegisterByWeekGenerator
        );
    }

    @Test
    void whenNull() {
        // When
        UserRetentionDataByWeekProvider actual = resolver.resolve(null);

        // Then
        assertThat(actual, is(pinVerificationGivenRegisterByWeekGenerator));
    }

    @Test
    void whenPinVerificationGivenRegister() {
        // When
        UserRetentionDataByWeekProvider actual = resolver.resolve(
            UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER
        );

        // Then
        assertThat(actual, is(pinVerificationGivenRegisterByWeekGenerator));
    }

    @Test
    void whenDigitalDocumentParticipationRateGivenRegister() {
        // When
        UserRetentionDataByWeekProvider actual = resolver.resolve(
            UserRetentionWeeklyCsvDataType.DIGITAL_DOCUMENT_PARTICIPATION_RATE_GIVEN_REGISTER
        );

        // Then
        assertThat(actual, is(digitalDocumentParticipationRateGivenRegisterByWeekGenerator));
    }

    @Test
    void whenPinVerificationGivenFirstDigitalDocumentComplete() {
        // When
        UserRetentionDataByWeekProvider actual = resolver.resolve(
            UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_FIRST_DIGITAL_DOCUMENT_COMPLETE
        );

        // Then
        assertThat(actual, is(pinVerificationGivenFirstDigitalDocumentCompleteByWeekGenerator));
    }

    @Test
    void whenPinVerificationGivenRegisterAndDigitalDocumentComplete() {
        // When
        UserRetentionDataByWeekProvider actual = resolver.resolve(
            UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE
        );

        // Then
        assertThat(actual, is(pinVerificationGivenRegisterAndDigitalDocumentCompleteByWeekGenerator));
    }

    @Test
    void whenPinVerificationGivenRegisterAndNoDigitalDocumentComplete() {
        // When
        UserRetentionDataByWeekProvider actual = resolver.resolve(
            UserRetentionWeeklyCsvDataType.PIN_VERIFICATION_GIVEN_REGISTER_AND_NO_DIGITAL_DOCUMENT_COMPLETE
        );

        // Then
        assertThat(actual, is(pinVerificationGivenRegisterAndNoDigitalDocumentCompleteByWeekGenerator));
    }

    @Test
    void nonRetainedUserGivenRegisterAndDigitalDocumentCompleteByWeekGenerator() {
        // When
        UserRetentionDataByWeekProvider actual = resolver.resolve(
            UserRetentionWeeklyCsvDataType.NON_RETAINED_USER_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE
        );

        // Then
        assertThat(actual, is(nonRetainedUserGivenRegisterAndDigitalDocumentCompleteByWeekGenerator));
    }

    @Test
    void nonRetainedUserGivenRegisterAndNoDigitalDocumentCompleteByWeekGenerator() {
        // When
        UserRetentionDataByWeekProvider actual = resolver.resolve(
            UserRetentionWeeklyCsvDataType.NON_RETAINED_USER_GIVEN_REGISTER_AND_NO_DIGITAL_DOCUMENT_COMPLETE
        );

        // Then
        assertThat(actual, is(nonRetainedUserGivenRegisterAndNoDigitalDocumentCompleteByWeekGenerator));
    }
}
