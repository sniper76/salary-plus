package ag.act.module.mydata;

import ag.act.dto.mydata.MyDataDto;
import ag.act.exception.BadRequestException;
import ag.act.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.security.GeneralSecurityException;

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class MyDataJsonReaderTest {

    @InjectMocks
    private MyDataJsonReader jsonReader;
    @Mock
    private ObjectMapperUtil objectMapperUtil;
    @Mock
    private MyDataCryptoHelper myDataCryptoHelper;
    @Mock
    private MyDataDto myDataDto;
    private String encodedJsonContent;
    private String decodedJsonContent;
    private MyDataDto actualMyDataDto;

    @BeforeEach
    void setUp() throws GeneralSecurityException, JsonProcessingException {
        encodedJsonContent = someString(10);
        decodedJsonContent = someString(20);

        given(myDataCryptoHelper.decrypt(encodedJsonContent)).willReturn(decodedJsonContent);
        given(objectMapperUtil.readValue(decodedJsonContent, MyDataDto.class)).willReturn(myDataDto);

    }

    @Nested
    class Success {

        @BeforeEach
        void setUp() {
            actualMyDataDto = jsonReader.readEncodedMyData(encodedJsonContent);
        }

        @Test
        void shouldReturnMyData() {
            assertThat(actualMyDataDto, is(myDataDto));
        }

        @Test
        void shouldDecryptEncodedJsonContent() throws GeneralSecurityException {
            then(myDataCryptoHelper).should().decrypt(encodedJsonContent);
        }

        @Test
        void shouldMapJsonToMyDataDto() throws JsonProcessingException {
            then(objectMapperUtil).should().readValue(decodedJsonContent, MyDataDto.class);
        }
    }

    @Nested
    class WhenFailToDecrypt {
        @BeforeEach
        void setUp() throws GeneralSecurityException {
            given(myDataCryptoHelper.decrypt(encodedJsonContent)).willThrow(GeneralSecurityException.class);
        }

        @Test
        void shouldThrowBadRequestException() {
            assertException(
                BadRequestException.class,
                () -> jsonReader.readEncodedMyData(encodedJsonContent),
                "마이데이터 파일을 읽을 수 없습니다."
            );
        }
    }

    @Nested
    class WhenFailToMap {
        @BeforeEach
        void setUp() throws JsonProcessingException {
            given(objectMapperUtil.readValue(decodedJsonContent, MyDataDto.class)).willThrow(JsonProcessingException.class);
        }

        @Test
        void shouldThrowBadRequestException() {
            assertException(
                BadRequestException.class,
                () -> jsonReader.readEncodedMyData(encodedJsonContent),
                "마이데이터 파일을 변환할 수 없습니다."
            );
        }
    }
}
