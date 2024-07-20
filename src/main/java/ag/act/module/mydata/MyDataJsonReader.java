package ag.act.module.mydata;

import ag.act.dto.mydata.MyDataDto;
import ag.act.exception.BadRequestException;
import ag.act.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;

@Slf4j
@Service
public class MyDataJsonReader {
    private final ObjectMapperUtil objectMapperUtil;
    private final MyDataCryptoHelper myDataCryptoHelper;

    public MyDataJsonReader(ObjectMapperUtil objectMapperUtil, MyDataCryptoHelper myDataCryptoHelper) {
        this.objectMapperUtil = objectMapperUtil;
        this.myDataCryptoHelper = myDataCryptoHelper;
    }

    private String decode(String jsonContent) throws GeneralSecurityException {
        return myDataCryptoHelper.decrypt(jsonContent);
    }

    public MyDataDto readEncodedMyData(String encodedJsonContent) {

        try {
            final String jsonContent = decode(encodedJsonContent);
            return objectMapperUtil.readValue(jsonContent, MyDataDto.class);
        } catch (JsonProcessingException e) {
            log.error("Can not read the MyData JsonContent :: {}", e.getMessage(), e);
            throw new BadRequestException("마이데이터 파일을 변환할 수 없습니다.", e);
        } catch (GeneralSecurityException e) {
            log.error("Can not decrypt the MyData JsonContent :: {}", e.getMessage(), e);
            throw new BadRequestException("마이데이터 파일을 읽을 수 없습니다.", e);
        } catch (Exception e) {
            log.error("Error occurred while reading the MyData JsonContent :: {}", e.getMessage(), e);
            throw new BadRequestException("마이데이터 파일을 읽는 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }
}
