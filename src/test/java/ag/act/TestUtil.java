package ag.act;

import ag.act.converter.DateTimeConverter;
import ag.act.enums.ActErrorCode;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.enums.virtualboard.VirtualBoardCategory;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import ag.act.exception.ActRuntimeException;
import ag.act.model.Gender;
import ag.act.model.Status;
import org.hamcrest.Matcher;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.junit.jupiter.api.function.Executable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ag.act.test.matcher.ApproximatelyEqual.approximatelyEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@SuppressWarnings("unchecked")
public class TestUtil {

    private static final List<BoardCategory> DEFAULT_EXCLUDE_BOARD_CATEGORIES = List.of(
        BoardCategory.SOLIDARITY_LEADER_ELECTION,
        BoardCategory.HOLDER_LIST_READ_AND_COPY
    );

    private static final BoardGroup[] NON_ADMIN_USER_WRITABLE_BOARD_GROUP = new BoardGroup[] {
        BoardGroup.GLOBALCOMMUNITY,
        BoardGroup.ANALYSIS,
        BoardGroup.DEBATE
    };

    public static String toIso8601Format(String yyyyMMddDateString) {
        final ZonedDateTime yyyyMMddDate = LocalDate.parse(yyyyMMddDateString, DateTimeFormatter.ofPattern("yyyyMMdd"))
            .atStartOfDay()
            .atZone(ZoneId.systemDefault());
        return yyyyMMddDate.format(DateTimeFormatter.ISO_INSTANT);
    }

    public static String someEmail() {
        return someEmail("test." + someAlphanumericString(10));
    }

    public static String someEmail(String username) {
        return username + "@gmail.com";
    }

    public static LocalDateTime someBirthDay() {
        return LocalDateTime.now()
            .minusYears(someIntegerBetween(20, 50))
            .minusDays(someIntegerBetween(0, 365))
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);
    }

    public static LocalDateTime someLocalDateTime() {
        return LocalDateTime.now()
            .plusYears(someIntegerBetween(-2, 2))
            .plusMonths(someIntegerBetween(-5, 5))
            .plusDays(someIntegerBetween(-15, 15))
            .withHour(someIntegerBetween(0, 23))
            .withMinute(someIntegerBetween(0, 59))
            .withSecond(someIntegerBetween(0, 59))
            .withNano(0);
    }

    public static LocalDate someLocalDate() {
        return LocalDate.now()
            .plusYears(someIntegerBetween(-2, 2))
            .plusMonths(someIntegerBetween(-5, 5))
            .plusDays(someIntegerBetween(-15, 15));
    }

    public static LocalDateTime someLocalDateTimeInThePast() {
        return LocalDateTime.ofInstant(
            Instant.now()
                .minus(someIntegerBetween(1, 365), ChronoUnit.DAYS)
                .minus(someIntegerBetween(1, 60), ChronoUnit.HOURS)
                .minus(someIntegerBetween(1, 60), ChronoUnit.MINUTES)
                .minus(someIntegerBetween(1, 60), ChronoUnit.SECONDS)
                .minus(someIntegerBetween(1, 60), ChronoUnit.MILLIS),
            ZoneOffset.systemDefault()
        );
    }

    public static LocalDateTime someLocalDateTimeInThePastDaysBetween(Integer min, Integer max) {
        return LocalDateTime.ofInstant(
            Instant.now()
                .minus(someIntegerBetween(min, max), ChronoUnit.DAYS)
                .minus(someIntegerBetween(1, 23), ChronoUnit.HOURS)
                .minus(someIntegerBetween(1, 60), ChronoUnit.MINUTES)
                .minus(someIntegerBetween(1, 60), ChronoUnit.SECONDS)
                .minus(someIntegerBetween(1, 60), ChronoUnit.MILLIS),
            ZoneOffset.systemDefault()
        );
    }

    public static LocalDateTime someLocalDateTimeInThePastMinutesBetween(Integer min, Integer max) {
        return LocalDateTime.ofInstant(
            Instant.now().minus(someIntegerBetween(min, max), ChronoUnit.MINUTES),
            ZoneOffset.systemDefault()
        );
    }

    public static LocalDateTime someLocalDateTimeInThePastSecondsBetween(Integer min, Integer max) {
        return LocalDateTime.ofInstant(
            Instant.now().minus(someIntegerBetween(min, max), ChronoUnit.SECONDS),
            ZoneOffset.systemDefault()
        );
    }

    public static LocalDateTime someLocalDateTimeInTheFuture() {
        return LocalDateTime.ofInstant(someInstantInTheFuture(), ZoneOffset.systemDefault());
    }

    public static Instant someInstantInTheFuture() {
        return Instant.now()
            .plus(someIntegerBetween(1, 365), ChronoUnit.DAYS)
            .plus(someIntegerBetween(1, 60), ChronoUnit.HOURS)
            .plus(someIntegerBetween(1, 60), ChronoUnit.MINUTES)
            .plus(someIntegerBetween(1, 60), ChronoUnit.SECONDS)
            .plus(someIntegerBetween(1, 60), ChronoUnit.MILLIS);
    }

    public static LocalDateTime someLocalDateTimeInTheFutureDaysBetween(Integer min, Integer max) {
        return LocalDateTime.ofInstant(
            Instant.now()
                .plus(someIntegerBetween(min, max), ChronoUnit.DAYS)
                .plus(someIntegerBetween(1, 23), ChronoUnit.HOURS)
                .plus(someIntegerBetween(1, 60), ChronoUnit.MINUTES)
                .plus(someIntegerBetween(1, 60), ChronoUnit.SECONDS)
                .plus(someIntegerBetween(1, 60), ChronoUnit.MILLIS),
            ZoneOffset.systemDefault()
        );
    }

    public static LocalDateTime someLocalDateTimeInTheFutureMinutesBetween(Integer min, Integer max) {
        return LocalDateTime.ofInstant(
            Instant.now().plus(someIntegerBetween(min, max), ChronoUnit.MINUTES),
            ZoneOffset.systemDefault()
        );
    }


    public static String someBirthDayString() {
        return someBirthDay().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static Integer getFirstNumberOfIdentification(String birthDate, Gender gender) {
        String thresholdDate = "20000101";
        if (birthDate.compareTo(thresholdDate) < 0) {
            return gender == Gender.M ? 1 : 2;
        }

        return gender == Gender.M ? 3 : 4;
    }

    public static BoardGroup someBoardGroupForGlobal() {
        return someThing(BoardGroup.getGlobalBoardGroups().toArray(new BoardGroup[0]));
    }

    public static BoardGroupCategory someBoardGroupCategoryForGlobal() {
        BoardGroup boardGroup = someBoardGroupForGlobal();
        return new BoardGroupCategory(boardGroup, someBoardCategoryExcluding(boardGroup));
    }

    public static BoardGroupCategory someBoardGroupCategoryForStock() {
        BoardGroup boardGroup = someBoardGroupForStock();
        return new BoardGroupCategory(boardGroup, someBoardCategoryExcluding(boardGroup));
    }

    public static BoardGroup someBoardGroupForStock() {
        return someThing(BoardGroup.getStockBoardGroups().toArray(BoardGroup[]::new));
    }

    public static BoardCategory someBoardCategoryForStocks() {
        return someBoardCategoryExcluding(
            Arrays.stream(BoardCategory.activeBoardCategoriesForStocks()),
            new BoardCategory[] {}
        );
    }

    public static BoardGroupCategory someNonAdminUserWritableBoardGroupCategory() {
        BoardGroup boardGroup = someNonAdminUserWritableBoardGroup();
        BoardCategory boardCategory = someBoardCategoryExcluding(boardGroup);
        return new BoardGroupCategory(boardGroup, boardCategory);
    }

    private static BoardGroup someNonAdminUserWritableBoardGroup() {
        return someThing(NON_ADMIN_USER_WRITABLE_BOARD_GROUP);
    }

    public static String someScript() {
        return "<script>%s</script><script language='javascript'>%s</script>".formatted(
            someAlphanumericString(10),
            someAlphanumericString(20)
        );
    }

    public static TestHtmlContent someHtmlContent() {
        return someHtmlContent(Boolean.FALSE);
    }

    public static TestHtmlContent someHtmlContent(Boolean isEd) {
        String tagName = someThing("테스트", "주주", "테스트왓", "테스트더", "헥");
        String tagValue = someAlphanumericString(5);
        String scriptValue = someAlphanumericString(5);

        if (isEd) {
            String html = "<div>&lt;div&gt;&lt;%s&gt;%s&lt;/div&gt;&lt;script&gt;%s&lt;/script&gt;<script>%s</script></div>"
                .formatted(tagName, tagValue, scriptValue, scriptValue);
            String expectedHtml = "<div>&lt;div&gt;&lt;%s&gt;%s&lt;/div&gt;&lt;script&gt;%s&lt;/script&gt;</div>"
                .formatted(tagName, tagValue, scriptValue);

            return new TestHtmlContent(html, expectedHtml);
        } else {
            String html = "<div>\n\n<%s>%shttps://www.test.co.kr/view?code=freeb&No=3108622</div><script>%s</script>".formatted(tagName, tagValue, scriptValue);
            String expectedHtml = "&lt;div&gt;\n\n&lt;%s&gt;%shttps://www.test.co.kr/view?code=freeb&No=3108622&lt;/div&gt;&lt;script&gt;%s&lt;/script&gt;"
                .formatted(tagName, tagValue, scriptValue);

            return new TestHtmlContent(html, expectedHtml);
        }

    }

    private static String toSafeHtml(String html) {
        return Jsoup.clean(html, Safelist.relaxed());
    }

    public record TestHtmlContent(String html, String expectedHtml) {
    }

    public record BoardGroupCategory(BoardGroup boardGroup, BoardCategory boardCategory) {
    }

    public static BoardCategory someBoardCategoryExceptDebate() {
        return someBoardCategoryExcluding(
            Arrays.stream(BoardCategory.activeBoardCategoriesForStocks()),
            new BoardCategory[] {BoardCategory.DEBATE}
        );
    }

    public static BoardCategory someBoardCategory(BoardGroup boardGroup) {
        return someBoardCategoryExcluding(boardGroup);
    }

    public static BoardCategory someBoardCategoryExcluding(BoardGroup boardGroup, BoardCategory... exceptBoardCategories) {
        return someBoardCategoryExcluding(
            boardGroup.getCategories().stream(),
            exceptBoardCategories
        );
    }

    public static BoardCategory someBoardCategoryExcluding(BoardCategory... exceptBoardCategories) {
        return someBoardCategoryExcluding(
            Arrays.stream(BoardCategory.activeBoardCategories()),
            exceptBoardCategories
        );
    }

    private static BoardCategory someBoardCategoryExcluding(Stream<BoardCategory> categoryStream, BoardCategory[] exceptBoardCategories) {
        final Set<BoardCategory> exceptBoardCategorySet = getExceptBoardCategorySet(exceptBoardCategories);
        return someThing(
            categoryStream
                .filter(it -> !exceptBoardCategorySet.contains(it))
                .toArray(BoardCategory[]::new)
        );
    }

    private static Set<BoardCategory> getExceptBoardCategorySet(BoardCategory[] exceptBoardCategories) {
        Set<BoardCategory> exceptBoardCategorySet = createSetFromOptionalArray(exceptBoardCategories);
        exceptBoardCategorySet.addAll(DEFAULT_EXCLUDE_BOARD_CATEGORIES);

        return exceptBoardCategorySet;
    }

    private static Set<BoardCategory> createSetFromOptionalArray(BoardCategory[] exceptBoardCategories) {
        return Optional.ofNullable(exceptBoardCategories)
            .map(Arrays::asList)
            .map(HashSet::new)
            .orElseGet(HashSet::new);
    }

    public static Status somePostStatus() {
        return someThing(Status.ACTIVE, Status.INACTIVE_BY_ADMIN, Status.DELETED_BY_ADMIN, Status.DELETED_BY_USER);
    }

    public static DigitalDocumentType someDigitalDocumentTypeExceptEtcDocument() {
        return someThing(DigitalDocumentType.DIGITAL_PROXY, DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT);
    }

    public static String someHtmlContentWithImages(String imageUrl1, String imageUrl2) {
        return toSafeHtml("<p><img src=\"%s\" /></p><p><img src=\"%s\" /></p>".formatted(imageUrl1, imageUrl2));
    }

    public static <T> Page<T> somePage(List<T> items) {
        return new PageImpl<>(items);
    }

    public static String someStockCode() {
        // 간헐적으로 동일한 종목코드가 생성되는 경우가 있어서, 테스트에서는 30 자리를 사용한다.
        return someNumericString(30);
    }

    public static String someCorporateNo() {
        return IntStream.range(0, someIntegerBetween(2, 4))
            .mapToObj(i -> someNumericString(someIntegerBetween(3, 10)))
            .collect(Collectors.joining("-"));
    }

    public static String someCorporateNoOverLength() {
        return IntStream.range(0, someIntegerBetween(2, 4))
            .mapToObj(i -> someNumericString(someIntegerBetween(256, 257)))
            .collect(Collectors.joining("-"));
    }

    public static String someIpAddress() {
        return "%s.%s.%s.%s".formatted(
            someIntegerBetween(1, 255),
            someIntegerBetween(0, 255),
            someIntegerBetween(0, 255),
            someIntegerBetween(0, 255)
        );
    }

    public static ZonedDateTime someKoreanTimeInToday() {
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
            .withSecond(someIntegerBetween(0, 59))
            .withMinute(someIntegerBetween(0, 59))
            .withHour(someIntegerBetween(0, 23));
    }

    public static String somePhoneNumber() {
        return "010" + someNumericString(8);
    }

    public static String somePhoneProvider() {
        return someThing("01", "02", "03", "04", "05", "06");
    }

    public static String replacePlaceholders(String templateString, Object... values) {
        String temp = templateString;

        for (Object value : values) {
            final String replacement = String.valueOf(value);
            final int i1 = temp.indexOf("{");
            final int i2 = temp.indexOf("}");
            final String placeholder = temp.substring(i1, i2 + 1);
            temp = temp.replace(placeholder, replacement);
        }

        return temp;
    }

    public static String someFilename() {
        return "IT_TEST_%s.%s".formatted(someAlphanumericString(50), someThing("png", "jpg", "jpeg"));
    }

    public static MockMultipartFile createMockMultipartFile(String fileKey) {
        return createMockMultipartFile(fileKey, MediaType.IMAGE_PNG_VALUE);
    }

    public static MockMultipartFile createMockMultipartFile(String fileKey, String contentType) {
        return createMockMultipartFile(fileKey, someFilename(), contentType);
    }

    public static MockMultipartFile createMockMultipartFile(String fileKey, String originalFilename, String contentType) {
        return new MockMultipartFile(fileKey, originalFilename, contentType, "test image".getBytes());
    }

    public static String someCompanyRegistrationNumber() {
        return someNumericString(5) + "-" + someNumericString(5);
    }

    public static <T extends Throwable> T assertException(Class<T> expectedType, Executable executable, String exceptionMessage) {
        final T exception = assertThrows(expectedType, executable);

        assertThat(exception.getMessage(), is(exceptionMessage));

        return exception;
    }

    public static <T extends ActRuntimeException> T assertException(
        Class<T> expectedType,
        Executable executable,
        String exceptionMessage,
        ActErrorCode actErrorCode
    ) {
        return assertException(expectedType, executable, exceptionMessage, actErrorCode.getCode());
    }

    public static <T extends ActRuntimeException> T assertException(
        Class<T> expectedType,
        Executable executable,
        String exceptionMessage,
        ActErrorCode actErrorCode,
        Map<String, Object> errorData
    ) {
        return assertException(expectedType, executable, exceptionMessage, actErrorCode.getCode(), errorData);
    }

    public static <T extends ActRuntimeException> T assertException(
        Class<T> expectedType,
        Executable executable,
        String exceptionMessage,
        Integer errorCode
    ) {
        final T exception = assertThrows(expectedType, executable);

        assertThat(exception.getMessage(), is(exceptionMessage));
        assertThat(exception.getErrorCode(), is(errorCode));

        return exception;
    }

    public static <T extends ActRuntimeException> T assertException(
        Class<T> expectedType,
        Executable executable,
        String exceptionMessage,
        Integer errorCode,
        Map<String, Object> errorData
    ) {
        final T exception = assertThrows(expectedType, executable);

        assertThat(exception.getMessage(), is(exceptionMessage));
        assertThat(exception.getErrorCode(), is(errorCode));
        assertThat(exception.getErrorData(), is(errorData));

        return exception;
    }

    @SuppressWarnings("rawtypes")
    public static MultiValueMap<String, String> toMultiValueMap(Map<String, Object> map) {

        final MultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<>();

        map.forEach((key, value) -> {
            if (value instanceof List) {
                linkedMultiValueMap.put(key, (List) value);
            } else {
                linkedMultiValueMap.add(key, value.toString());
            }
        });

        return linkedMultiValueMap;
    }

    public static String someWebVerificationCode() {
        final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        final Random random = new Random(System.currentTimeMillis());

        char letter = ALPHABET[random.nextInt(ALPHABET.length)];
        int number = random.nextInt(1000);

        return String.format("%c%03d", letter, number);
    }

    public static String someStrongPassword() {
        return someStrongPassword(8, 20);
    }

    public static String someStrongPassword(int minLength, int maxLength) {
        if (minLength < 1 || maxLength < minLength) {
            throw new IllegalArgumentException("Invalid length parameters");
        }

        String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerChars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()_-+=<>?";

        String allChars = upperChars + lowerChars + numbers + specialChars;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(upperChars.charAt(random.nextInt(upperChars.length())));
        password.append(lowerChars.charAt(random.nextInt(lowerChars.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        int passwordLength = minLength + random.nextInt(maxLength - minLength + 1);
        for (int i = 4; i < passwordLength; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        return shuffleString(password.toString());
    }

    private static String shuffleString(String input) {
        char[] chars = input.toCharArray();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < chars.length; i++) {
            int randomIndex = random.nextInt(chars.length);
            char temp = chars[i];
            chars[i] = chars[randomIndex];
            chars[randomIndex] = temp;
        }
        return new String(chars);
    }

    public static String getDigitalDocumentTemplatePath() {
        try {
            return FileSystems.getDefault()
                .getPath("./src/main/resources/templates/digitaldocument")
                .toUri().toURL().toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException("[TEST] failed to getDigitalDocumentTemplatePath()", e);
        }
    }

    @SuppressWarnings("ConstantValue")
    public static void assertTime(LocalDate actual, LocalDate expected) {
        if (actual == null) {
            assertThat(actual, is(expected));
            return;
        }
        assertTime(actual.atStartOfDay(), expected.atStartOfDay());
    }

    @SuppressWarnings("ConstantValue")
    public static void assertTime(LocalDateTime actual, Instant expected) {
        if (actual == null) {
            assertThat(actual, is(expected));
            return;
        }
        assertTime(actual, DateTimeConverter.convert(expected));
    }

    @SuppressWarnings("ConstantValue")
    public static void assertTime(Instant actual, LocalDateTime expected) {
        if (actual == null) {
            assertThat(actual, is(expected));
            return;
        }
        assertTime(DateTimeConverter.convert(actual), expected);
    }

    @SuppressWarnings("ConstantValue")
    public static void assertTime(Instant actual, Instant expected) {
        if (actual == null) {
            assertThat(actual, is(expected));
            return;
        }
        assertThat(actual.toEpochMilli(), approximatelyEqual(expected.toEpochMilli(), 1D));
    }

    @SuppressWarnings("ConstantValue")
    public static void assertTime(LocalDateTime actual, LocalDateTime expected) {
        if (actual == null) {
            assertThat(actual, is(expected));
            return;
        }
        assertThat(actual.toEpochSecond(ZoneOffset.UTC), approximatelyEqual(expected.toEpochSecond(ZoneOffset.UTC), 1D));
    }

    public static <T> void assertTime(T actual, Matcher<? super T> matcher) {
        assertThat("", actual, matcher);
    }

    public static <T> List<T> shuffleAndGet(T... items) {
        if (items == null) {
            return Collections.emptyList();
        }

        return shuffleAndGet(Arrays.asList(items));
    }

    public static <T> List<T> shuffleAndGet(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        List<T> newList = new ArrayList<>(list);
        Collections.shuffle(newList);

        return newList;
    }

    public static String getPathFrom(String name) {
        return name.toLowerCase().replace("_", "-");
    }

    public static String someSolidarityLeaderElectionApplicationItem() {
        return someString(80, 500);
    }

    public static SolidarityLeaderElectionApplyStatus someSolidarityLeaderElectionApplyStatus() {
        return someThing(SolidarityLeaderElectionApplyStatus.SAVE, SolidarityLeaderElectionApplyStatus.COMPLETE);
    }

    public static SolidarityLeaderElectionStatus someSolidarityLeaderElectionOngoingStatus() {
        return someThing(
            SolidarityLeaderElectionStatus.CANDIDATE_REGISTRATION_PERIOD,
            SolidarityLeaderElectionStatus.VOTE_PERIOD
        );
    }

    public static BoardCategory someBoardCategoryFromVirtualBoardCategory(VirtualBoardCategory virtualBoardCategory) {
        return someThing(virtualBoardCategory.getSubCategories().toArray(BoardCategory[]::new));
    }

    public static VirtualBoardCategory someVirtualBoardCategory(VirtualBoardGroup virtualBoardGroup) {
        return someThing(virtualBoardGroup.getVirtualCategories().toArray(VirtualBoardCategory[]::new));
    }

    public static BoardCategory someBoardCategoryFromVirtualBoardCategoryExcluding(VirtualBoardCategory virtualBoardCategory) {
        Stream<BoardCategory> boardCategoryStream = Arrays.stream(VirtualBoardCategory.values())
            .map(VirtualBoardCategory::getSubCategories)
            .flatMap(List::stream);

        BoardCategory[] exceptBoardCategories = virtualBoardCategory.getSubCategories().toArray(BoardCategory[]::new);

        return someBoardCategoryExcluding(boardCategoryStream, exceptBoardCategories);
    }

    public static PushSendStatus someReadyOrCompletePushSendStatus() {
        return someThing(PushSendStatus.READY, PushSendStatus.COMPLETE);
    }
}
