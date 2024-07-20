package ag.act.core.infra;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class LambdaEnvironment {

    private final String zipFilesLambdaName;

    public LambdaEnvironment(
        @Value("${aws.lambda.zip-file.name:act-zip-files-dev}")
        String zipFilesLambdaName
    ) {
        this.zipFilesLambdaName = zipFilesLambdaName;
    }
}
