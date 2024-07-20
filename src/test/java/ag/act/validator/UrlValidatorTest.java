package ag.act.validator;

import ag.act.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import shiver.me.timbers.data.random.RandomStrings;

import static ag.act.TestUtil.assertException;
import static org.assertj.core.api.Assertions.assertThatCode;

public class UrlValidatorTest {
    private UrlValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UrlValidator();
    }

    @Nested
    class WhenEverythingIsFine {
        @Test
        void shouldNotThrowAnyException() {
            assertThatCode(() -> validator.validate("https://www.google.com"))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    class WhenHaveSpace {
        @Test
        void shouldNotThrowAnyException() {
            assertThatCode(() -> validator.validate("      https://www.google.com        "))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    class WhenUrlIsNull {
        @Test
        void shouldThrowBadRequestException() {
            whenUrlIsBlank(null);
        }

    }

    @Nested
    class WhenUrlIsEmpty {
        @Test
        void shouldThrowBadRequestException() {
            whenUrlIsBlank("");
        }

    }

    @Nested
    class WhenUrlIsSpace {
        @Test
        void shouldThrowBadRequestException() {
            whenUrlIsBlank(" ");
        }

    }

    @Nested
    class WhenUrlIsWrong {
        @Test
        void shouldThrowInternalServerException() {
            assertException(
                BadRequestException.class,
                () -> validator.validate(RandomStrings.someAlphanumericString(20)),
                "url 형식이 잘못되었습니다. 올바른 url을 입력해주세요."
            );
        }
    }

    private void whenUrlIsBlank(String blankUrl) {
        assertException(
            BadRequestException.class,
            () -> validator.validate(blankUrl),
            "url을 확인해주세요."
        );
    }
}
