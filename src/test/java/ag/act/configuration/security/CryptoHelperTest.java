package ag.act.configuration.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class CryptoHelperTest {

    private CryptoHelper cryptoHelper;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException, InvalidKeySpecException {
        final String secretToken = someString(64);
        final String secretSalt = someString(64);

        cryptoHelper = new CryptoHelper(secretToken, secretSalt);
    }

    @Test
    void shouldEncryptAndDecrypt() throws Exception {

        // Given
        final String input = "01021633431";

        // When
        final String actual = cryptoHelper.encrypt(input);
        final String decrypt = cryptoHelper.decrypt(actual);

        // Then
        assertThat(decrypt, is(input));
    }
}