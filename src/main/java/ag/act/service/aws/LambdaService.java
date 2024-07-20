package ag.act.service.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

@Slf4j
@Service
public class LambdaService {

    private final LambdaClient lambdaClient;

    @Autowired
    public LambdaService(LambdaClient lambdaClient) {
        this.lambdaClient = lambdaClient;
    }

    public void invokeLambdaAsync(String functionName, String payload) {
        log.info("Lambda functionName : {}, payload : {}", functionName, payload);

        InvokeRequest request = InvokeRequest.builder()
            .functionName(functionName)
            .invocationType(InvocationType.EVENT)
            .payload(SdkBytes.fromByteArray(payload.getBytes()))
            .build();

        log.info("Lambda functionName : {} before", functionName);
        InvokeResponse invokeResponse = lambdaClient.invoke(request);
        log.info("Lambda functionName : {} after", functionName);


        if (isSuccess(invokeResponse)) {
            log.info("Lambda response statusCode: {}", invokeResponse.statusCode());
        } else {
            log.error("Lambda error statusCode: {}, response: {}", invokeResponse.statusCode(), invokeResponse.functionError());
        }

    }

    private static boolean isSuccess(InvokeResponse invokeResponse) {
        return invokeResponse.statusCode() >= 200 && invokeResponse.statusCode() < 300;
    }
}
