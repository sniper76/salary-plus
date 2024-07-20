package ag.act.module.auth.web;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class WebVerificationCodeUtil implements WebVerificationBase {

    public String generateEvenCode() {
        final Random random = createRandom();

        return format(
            getRandomLetter(random),
            getRandomEvenNumber(random)
        );
    }

    public String generateOddCode() {
        final Random random = createRandom();

        return format(
            getRandomLetter(random),
            getRandomOddNumber(random)
        );
    }

    private Random createRandom() {
        return new Random(System.currentTimeMillis());
    }

    private String format(final char letter, final int number) {
        return "%c%03d".formatted(letter, number);
    }

    private char getRandomLetter(final Random random) {
        return VERIFICATION_CODE_ALPHABET[random.nextInt(VERIFICATION_CODE_ALPHABET.length)];
    }

    private int getRandomOddNumber(final Random random) {
        while (true) {
            int number = random.nextInt(VERIFICATION_CODE_MAX_NUMBER_BOUND);
            if (number % 2 == 1) {
                return number;
            }
        }
    }

    private int getRandomEvenNumber(final Random random) {
        while (true) {
            int number = random.nextInt(VERIFICATION_CODE_MAX_NUMBER_BOUND);
            if (number % 2 == 0) {
                return number;
            }
        }
    }
}
