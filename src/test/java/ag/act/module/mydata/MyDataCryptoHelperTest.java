package ag.act.module.mydata;

import ag.act.module.mydata.crypto.MyDataCryptoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.security.GeneralSecurityException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class MyDataCryptoHelperTest {

    @InjectMocks
    private MyDataCryptoHelper helper;

    @Mock
    private MyDataConfig myDataConfig;

    @BeforeEach
    void setUp() {
        given(myDataConfig.getAes256key()).willReturn(someAlphanumericString(32));
    }

    @Test
    void shouldEncryptAndDecrypt() throws GeneralSecurityException {

        // Given
        final String str = someString(50);

        // When
        final String encrypted = helper.encrypt(str);

        // Then
        assertThat(helper.decrypt(encrypted), is(str));
    }
}

