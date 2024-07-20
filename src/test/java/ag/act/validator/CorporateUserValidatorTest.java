package ag.act.validator;

import ag.act.exception.BadRequestException;
import ag.act.validator.user.CorporateUserValidator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.stream.Stream;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someCorporateNo;
import static ag.act.TestUtil.someCorporateNoOverLength;

@MockitoSettings(strictness = Strictness.LENIENT)
class CorporateUserValidatorTest {

    @InjectMocks
    private CorporateUserValidator validator;

    @Nested
    class WhenCreate {

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of(someCorporateNo()),
                Arguments.of("1231212345"),
                Arguments.of("1234-5678-9000"),
                Arguments.of("1234-5678-90-00"),
                Arguments.of("123456789000"),
                Arguments.of("12345678-9000"),
                Arguments.of("1-2234234234234-3-3-3-3-3-2342342343-3-3-3-3"),
                Arguments.of("111111-111111112121212-1212124140140124012-4-124-12-4-124-1-241")
            );
        }

        @ParameterizedTest(name = "{index} => corporateNo=''{0}''")
        @MethodSource("valueProvider")
        void shouldValidateCorporateNo(String corporateNo) {
            validator.validateCorporateNo(corporateNo);
        }
    }


    @Nested
    class WhenCorporateNoPatternError {

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of("ABCDEFGHIJ"),
                Arguments.of("1231A-12C45"),
                Arguments.of("-1231-1245"),
                Arguments.of("1231-1245-"),
                Arguments.of("-1231-1245-"),
                Arguments.of("1231A--12C45"),
                Arguments.of("여기부터-에러-케이스"),
                Arguments.of("11111-"),
                Arguments.of("-12312-1231231-23-1231"),
                Arguments.of("12312-1231231-23-1231a"),
                Arguments.of("1111----1111111-11121313131313"),
                Arguments.of("sdfjsldf-sdfljsdfls"),
                Arguments.of("123123-123123123-"),
                Arguments.of("-392340234-2342342-2342"),
                Arguments.of("-sfjsdfisdi"),
                Arguments.of("sijfsf-"),
                Arguments.of("1231213132dd-dd0d02030"),
                Arguments.of(someCorporateNoOverLength())
            );
        }

        @ParameterizedTest(name = "{index} => corporateNo=''{0}''")
        @MethodSource("valueProvider")
        void shouldReturn(String corporateNo) {
            assertException(
                BadRequestException.class,
                () -> validator.validateCorporateNo(corporateNo),
                "법인사업자번호 형식이 맞지 않습니다."
            );
        }
    }
}
