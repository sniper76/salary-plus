package ag.act.service.digitaldocument.answer;

import ag.act.enums.DigitalAnswerType;
import ag.act.exception.BadRequestException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AnswerParser {

    private static final String ANSWERS_DELIMITER = ",";
    private static final String ANSWER_DELIMITER = ":";
    private static final int ONE_ANSWER_SPLIT_SIZE = 2;

    public List<Answer> parseAnswers(String answerData) {
        return Arrays.stream(answerData.split(ANSWERS_DELIMITER))
            .map(this::parseAnswer)
            .toList();
    }

    @NotNull
    private Answer parseAnswer(String answer) {
        final String[] data = answer.split(ANSWER_DELIMITER);

        if (data.length != ONE_ANSWER_SPLIT_SIZE) {
            throw new BadRequestException("찬성/반대/기권 정보를 확인하세요.");
        }

        return new Answer(
            Long.parseLong(data[0].trim()),
            DigitalAnswerType.fromValue(data[1].trim())
        );
    }
}
