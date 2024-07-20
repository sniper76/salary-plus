package ag.act.module.mydata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class MyDataCryptoHelperTest {

    private MyDataCryptoHelper helper;

    @BeforeEach
    void setUp() {
        helper = new MyDataCryptoHelper(someAlphanumericString(32));
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

    @Test
    @Disabled
    void shouldEncryptWithSecretKey() throws GeneralSecurityException {

        final String secretKey = someAlphanumericString(32);

        final String encrypted = new MyDataCryptoHelper(secretKey)
            .encrypt("""
                json
                """);

        System.out.println("__________________________________");
        System.out.println(encrypted);
        System.out.println("__________________________________");
        System.out.println(secretKey);
        System.out.println("__________________________________");
    }

    @Test
    @Disabled
    void shouldDecryptWithSecretKey() throws GeneralSecurityException, IOException {

        final String secretKey = someAlphanumericString(32);
        File file = new File("/Users/yanggun7201/Downloads/9_2023_08_23_10_39_09.json");
        String content = Files.readString(file.toPath());

        final String encrypted = new MyDataCryptoHelper(secretKey)
            .decrypt(content);

        System.out.println("__________________________________");
        System.out.println(encrypted);
        System.out.println("__________________________________");
        System.out.println(secretKey);
        System.out.println("__________________________________");
    }
}

