package ag.act.configuration.security;

import ag.act.util.DateTimeUtil;
import ag.act.util.TokenUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@MockitoSettings(strictness = Strictness.LENIENT)
class TokenProviderTest {

    private TokenProvider tokenProvider;

    private List<MockedStatic<?>> statics;
    @Mock
    private TokenUtil tokenUtil;
    private String subject;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));

        subject = someAlphanumericString(50);
        final String secretKey = someAlphanumericString(128);
        final long appExpirationDateInDays = someIntegerBetween(200, 360);
        final long webExpirationDateInDays = someIntegerBetween(1, 100);
        final Date appExpirationDate = new Date(Instant.now().plusSeconds(someIntegerBetween(100, 1000)).toEpochMilli());
        final Date webExpirationDate = new Date(Instant.now().plusSeconds(someIntegerBetween(1001, 2000)).toEpochMilli());
        final Date issuedAt = new Date();

        given(DateTimeUtil.newDate()).willReturn(issuedAt);
        given(DateTimeUtil.getFutureDateFromCurrentInstant(appExpirationDateInDays)).willReturn(appExpirationDate);
        given(DateTimeUtil.getFutureDateFromCurrentInstant(webExpirationDateInDays)).willReturn(webExpirationDate);

        tokenProvider = new TokenProvider(secretKey, appExpirationDateInDays, webExpirationDateInDays, tokenUtil);
    }

    @Test
    void shouldDifferentWebTokenAndAppToken() {
        // When
        String webToken = tokenProvider.createWebToken(subject);
        String appToken = tokenProvider.createAppToken(subject);

        // Then
        assertThat(webToken, not(appToken));
    }

    @Test
    void shouldTheSameTokensWithTheSameSubject() {
        // When
        String appToken1 = tokenProvider.createAppToken(subject);
        String appToken2 = tokenProvider.createAppToken(subject);
        String webToken1 = tokenProvider.createWebToken(subject);
        String webToken2 = tokenProvider.createWebToken(subject);

        // Then
        assertThat(appToken1, is(appToken2));
        assertThat(webToken1, is(webToken2));
    }

}
