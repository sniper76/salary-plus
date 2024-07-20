package ag.act.service.digitaldocument.download.csv;

import ag.act.converter.csv.CsvDataConverter;
import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.SimpleUserDto;
import ag.act.entity.digitaldocument.DigitalDocumentItemUserAnswer;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalAnswerType;
import ag.act.model.Gender;
import ag.act.service.download.csv.dto.DigitalDocumentCsvRecordInputDto;
import ag.act.service.download.csv.record.AdminDigitalDocumentCsvRecordGeneratorImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@MockitoSettings(strictness = Strictness.LENIENT)
class AdminDigitalDocumentCsvRecordGeneratorImplTest {

    @InjectMocks
    private AdminDigitalDocumentCsvRecordGeneratorImpl generator;
    private List<MockedStatic<?>> statics;
    @Mock
    private CsvDataConverter csvDataConverter;
    @Mock
    private DigitalDocumentUser digitalDocumentUser;
    @Mock
    private DigitalDocumentItemUserAnswer answer1;
    @Mock
    private DigitalDocumentItemUserAnswer answer2;
    private String[] actualValues;
    private String[] expectedResult;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(RequestContextHolder.class));

        final Long userId = someLong();
        final Long issuedNumber = someLongBetween(1L, 100L);
        final String userName = someAlphanumericString(10);
        final LocalDateTime birthday = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        final Gender gender = someEnum(Gender.class);
        final String address = someAlphanumericString(15);
        final String addressDetail = someAlphanumericString(20);
        final String zipcode = someAlphanumericString(25);
        final String stockName = someAlphanumericString(30);
        final Long stockCount = somePositiveLong();
        final Long purchasePrice = somePositiveLong();
        final Long longPrice = somePositiveLong();
        final LocalDateTime registeredAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        final List<DigitalDocumentItemUserAnswer> userAnswerDtoList = List.of(answer1, answer2);

        final DigitalAnswerType answerType1 = someEnum(DigitalAnswerType.class);
        final DigitalAnswerType answerType2 = someEnum(DigitalAnswerType.class);

        given(answer1.getAnswerSelectValue()).willReturn(answerType1);
        given(answer2.getAnswerSelectValue()).willReturn(answerType2);

        given(digitalDocumentUser.getUserId()).willReturn(userId);
        given(digitalDocumentUser.getIssuedNumber()).willReturn(issuedNumber);
        given(digitalDocumentUser.getName()).willReturn(userName);
        given(digitalDocumentUser.getBirthDate()).willReturn(birthday);
        given(digitalDocumentUser.getGender()).willReturn(gender);
        given(digitalDocumentUser.getAddress()).willReturn(address);
        given(digitalDocumentUser.getAddressDetail()).willReturn(addressDetail);
        given(digitalDocumentUser.getZipcode()).willReturn(zipcode);
        given(digitalDocumentUser.getStockName()).willReturn(stockName);
        given(digitalDocumentUser.getStockCount()).willReturn(stockCount);
        given(digitalDocumentUser.getPurchasePrice()).willReturn(purchasePrice);
        given(digitalDocumentUser.getLoanPrice()).willReturn(longPrice);

        final String issuedNumberString = someAlphanumericString(30);
        final String birthdayString = someAlphanumericString(35);
        final String genderString = someAlphanumericString(40);
        final String addressString = someAlphanumericString(45);
        final String addressDetailString = someAlphanumericString(50);
        final String zipcodeString = someAlphanumericString(55);
        final String phoneNumberString = someAlphanumericString(60);
        final String stockCountString = someAlphanumericString(65);
        final String purchasePriceString = someAlphanumericString(70);
        final String longPriceString = someAlphanumericString(75);
        final String updatedAtInKoreanTimeString = someAlphanumericString(80);
        final String registeredAtInKoreanTimeString = someAlphanumericString(35);

        expectedResult = List.of(
            issuedNumberString,
            userName,
            birthdayString,
            genderString,
            addressString,
            addressDetailString,
            zipcodeString,
            phoneNumberString,
            stockName,
            stockCountString,
            purchasePriceString,
            longPriceString,
            updatedAtInKoreanTimeString,
            registeredAtInKoreanTimeString,
            answerType1.getDisplayName(),
            answerType2.getDisplayName()
        ).toArray(String[]::new);

        given(csvDataConverter.getStringNumber(issuedNumber)).willReturn(issuedNumberString);
        given(csvDataConverter.getBirthday(birthday)).willReturn(birthdayString);
        given(csvDataConverter.getGender(digitalDocumentUser)).willReturn(genderString);
        given(csvDataConverter.getString(address)).willReturn(addressString);
        given(csvDataConverter.getString(addressDetail)).willReturn(addressDetailString);
        given(csvDataConverter.getString(zipcode)).willReturn(zipcodeString);
        given(csvDataConverter.getPhoneNumber(digitalDocumentUser)).willReturn(phoneNumberString);
        given(csvDataConverter.getStringNumber(stockCount)).willReturn(stockCountString);
        given(csvDataConverter.getStringNumber(purchasePrice)).willReturn(purchasePriceString);
        given(csvDataConverter.getStringNumber(longPrice)).willReturn(longPriceString);
        given(csvDataConverter.getUpdatedAtInKoreanTime(digitalDocumentUser)).willReturn(updatedAtInKoreanTimeString);
        given(csvDataConverter.getRegisteredAt(registeredAt)).willReturn(registeredAtInKoreanTimeString);

        actualValues = generator.toCsvRecord(new DigitalDocumentCsvRecordInputDto(
            digitalDocumentUser,
            userAnswerDtoList,
            () -> List.of(new SimpleUserDto(userId, LocalDateTime.now(), registeredAt, gender))
        ));
    }

    @Test
    void shouldReturnCorrectStringArray() {
        assertThat(actualValues, is(expectedResult));
    }
}
