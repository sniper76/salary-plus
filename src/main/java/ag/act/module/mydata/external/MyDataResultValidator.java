package ag.act.module.mydata.external;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.mydata.MyDataResultDto;
import ag.act.enums.MyDataError;
import ag.act.exception.InternalServerException;
import ag.act.module.mydata.IMyDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyDataResultValidator implements IMyDataService {

    public void validate(MyDataResultDto responseDto, String errorMessageFormat) {
        if (isSuccess(responseDto.getCode())) {
            return;
        }

        MyDataError mydataError = MyDataError.fromValue(responseDto.getCode());

        errorLog(responseDto, mydataError);

        throw new InternalServerException(errorMessageFormat.formatted(mydataError.getMessage()));
    }

    private void errorLog(MyDataResultDto responseDto, MyDataError mydataError) {
        log.error(
            "MyDataError: {} :: {} :: {} :: UserID:{}",
            mydataError,
            responseDto.getCode(),
            responseDto.getMessage(),
            ActUserProvider.getNoneNull().getId()
        );
    }

    private boolean isSuccess(String code) {
        return MY_DATA_SUCCESS_CODE.equals(code);
    }
}
