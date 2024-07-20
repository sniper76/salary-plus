package ag.act.service.user.download.csv;

import ag.act.converter.csv.CsvDataConverter;
import ag.act.dto.user.UserWithStockDto;
import ag.act.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserCsvRecordGenerator {

    private final CsvDataConverter csvDataConverter;

    public UserCsvRecordGenerator(CsvDataConverter csvDataConverter) {
        this.csvDataConverter = csvDataConverter;
    }

    public String[] toCsvRecord(int rowNum, UserWithStockDto userWithStockDto) {

        final User user = userWithStockDto.user();

        return new String[] {
            String.valueOf(rowNum),
            user.getName(),
            csvDataConverter.getBirthday(user.getBirthDate()),
            csvDataConverter.getString(user.getAddress()),
            csvDataConverter.getString(user.getAddressDetail()),
            csvDataConverter.getString(user.getZipcode()),
            csvDataConverter.getPhoneNumber(user.getHashedPhoneNumber()),
            csvDataConverter.getStringNumber(userWithStockDto.holdingStockCount())
        };
    }
}
