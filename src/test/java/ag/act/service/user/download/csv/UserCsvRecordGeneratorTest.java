package ag.act.service.user.download.csv;

import ag.act.converter.csv.CsvDataConverter;
import ag.act.dto.user.UserWithStockDto;
import ag.act.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserCsvRecordGeneratorTest {

    @InjectMocks
    private UserCsvRecordGenerator generator;
    @Mock
    private CsvDataConverter csvDataConverter;
    @Mock
    private UserWithStockDto userWithStockDto;
    @Mock
    private User user;

    private String[] actualValues;
    private String[] expectedResult;

    @BeforeEach
    void setUp() {
        final Integer rowNum = someIntegerBetween(1, 10);
        final String userName = someAlphanumericString(10);
        final LocalDateTime birthday = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        final String address = someAlphanumericString(15);
        final String addressDetail = someAlphanumericString(20);
        final String zipcode = someAlphanumericString(25);
        final Long stockCount = somePositiveLong();
        final String hashedPhoneNumber = someString(10);

        given(userWithStockDto.user()).willReturn(user);
        given(user.getName()).willReturn(userName);
        given(user.getBirthDate()).willReturn(birthday);
        given(user.getAddress()).willReturn(address);
        given(user.getAddressDetail()).willReturn(addressDetail);
        given(user.getZipcode()).willReturn(zipcode);
        given(user.getHashedPhoneNumber()).willReturn(hashedPhoneNumber);
        given(userWithStockDto.holdingStockCount()).willReturn(stockCount);

        final String birthdayString = someAlphanumericString(35);
        final String addressString = someAlphanumericString(45);
        final String addressDetailString = someAlphanumericString(50);
        final String zipcodeString = someAlphanumericString(55);
        final String phoneNumberString = someAlphanumericString(60);
        final String stockCountString = someAlphanumericString(65);

        expectedResult = List.of(
            String.valueOf(rowNum),
            userName,
            birthdayString,
            addressString,
            addressDetailString,
            zipcodeString,
            phoneNumberString,
            stockCountString
        ).toArray(String[]::new);

        given(csvDataConverter.getBirthday(birthday)).willReturn(birthdayString);
        given(csvDataConverter.getString(address)).willReturn(addressString);
        given(csvDataConverter.getString(addressDetail)).willReturn(addressDetailString);
        given(csvDataConverter.getString(zipcode)).willReturn(zipcodeString);
        given(csvDataConverter.getPhoneNumber(hashedPhoneNumber)).willReturn(phoneNumberString);
        given(csvDataConverter.getStringNumber(stockCount)).willReturn(stockCountString);

        actualValues = generator.toCsvRecord(rowNum, userWithStockDto);
    }

    @Test
    void shouldReturnCorrectStringArray() {
        assertThat(actualValues, is(expectedResult));
    }


}