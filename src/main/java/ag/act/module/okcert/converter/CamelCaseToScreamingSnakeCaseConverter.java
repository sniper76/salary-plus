package ag.act.module.okcert.converter;

import org.springframework.stereotype.Component;

@Component
public class CamelCaseToScreamingSnakeCaseConverter {
    public String convert(String input) {
        final StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                result.append('_').append(currentChar);
            } else {
                result.append(currentChar);
            }
        }

        return result.toString().toUpperCase();
    }
}
