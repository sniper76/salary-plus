package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.push.CreateFcmPushDataDto;
import ag.act.entity.BatchLog;
import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.enums.AutomatedPushContentType;
import ag.act.enums.AutomatedPushCriteria;
import ag.act.enums.CommentType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushTargetType;
import ag.act.enums.push.PushTopic;
import ag.act.enums.push.UserPushAgreementGroupType;
import ag.act.model.BatchRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.util.DateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings({"unchecked", "FieldCanBeLocal"})
class SendPushesBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/batch/send-pushes";
    private static final String BATCH_NAME = "SEND_PUSHES";
    public static final int REPLY_COMMENT_CRITERIA_1 = 1;

    private List<MockedStatic<?>> statics;

    private Map<String, Object> request;
    private Push push1;
    private Push push2;
    private Push push3;
    private Push push4;
    private Push push5;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User push6RefusedUser;
    private User user7AutoCommentAuthor;
    private User user8AutoPostAuthor;

    @SuppressWarnings("rawtypes")
    private final List<Supplier> assertions = new ArrayList<>();
    private Stock stock;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();

        dbCleaner.clean();

        stubStock();

        user1 = stubUserWithPushToken();
        user2 = stubUserWithPushToken();
        user3 = stubUserWithPushToken();
        user4 = stubUserWithPushToken();
        user5 = stubUserWithPushToken();
        push6RefusedUser = stubUserWithPushToken();
        user7AutoCommentAuthor = stubUserWithPushToken();
        user8AutoPostAuthor = stubUserWithPushToken();

        grantStockToUser(user1);
        grantStockToUser(user2);
        grantStockToUser(user3);
        grantStockToUser(push6RefusedUser);

        Stock stockAutoCommentAuthor = itUtil.createStock();
        grantStockToUser(user7AutoCommentAuthor, stockAutoCommentAuthor);

        Stock stockAutoPostAuthor = itUtil.createStock();
        grantStockToUser(user8AutoPostAuthor, stockAutoCommentAuthor);

        refusePush(push6RefusedUser);

        push1 = stubPushWithTargetAll();
        push2 = stubPushWithTargetStock();
        push3 = stubPushWithTargetStockGroup();
        push4 = stubPushWithAutomatedCommentPush(user7AutoCommentAuthor, stockAutoCommentAuthor);
        push5 = stubPushWithAutomatedPostPush(user8AutoPostAuthor, stockAutoPostAuthor);

        final int batchPeriod = 1;
        request = Map.of("batchPeriod", batchPeriod, "periodTimeUnit", BatchRequest.PeriodTimeUnitEnum.MINUTE.name());

        String date = someString(5);
        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusMinutes(batchPeriod)).willReturn(LocalDateTime.now());
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldSendPushes() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(batchXApiKey())
            )
            .andExpect(status().isOk())
            .andReturn();

        final SimpleStringResponse result = itUtil.getResult(response, SimpleStringResponse.class);

        assertResponse(result);
        assertResponse(itUtil.findPush(push1.getId()).orElseThrow(() -> new RuntimeException("[TEST] Push1 찾을 수 없습니다.")));
        assertResponse(itUtil.findPush(push2.getId()).orElseThrow(() -> new RuntimeException("[TEST] Push2 찾을 수 없습니다.")));
        assertResponse(itUtil.findPush(push3.getId()).orElseThrow(() -> new RuntimeException("[TEST] Push3 찾을 수 없습니다.")));
        assertResponse(itUtil.findPush(push4.getId()).orElseThrow(() -> new RuntimeException("[TEST] Push4 찾을 수 없습니다.")));
        assertResponse(itUtil.findPush(push5.getId()).orElseThrow(() -> new RuntimeException("[TEST] Push5 찾을 수 없습니다.")));
    }

    private void assertResponse(SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [sent: %s / %s]".formatted(BATCH_NAME, 5, 5);
        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME).orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));

        assertions.forEach(Supplier::get);
    }

    private void assertResponse(Push pushFromDatabase) {
        assertThat(pushFromDatabase.getSendStatus(), is(PushSendStatus.COMPLETE));
        assertThat(pushFromDatabase.getSentStartDatetime(), notNullValue());
        assertThat(pushFromDatabase.getSentEndDatetime(), notNullValue());
    }

    private void stubStock() {
        final String stockCode = someStockCode();
        final String stockName = someAlphanumericString(10);
        stock = itUtil.createStock(stockCode);
        stock.setName(stockName);
        stock = itUtil.updateStock(stock);
    }

    private User stubUserWithPushToken() {
        User user = itUtil.createUser();
        user.setPushToken(someAlphanumericString(30));
        return itUtil.updateUser(user);
    }

    private void grantStockToUser(User user) {
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private void grantStockToUser(User user, Stock stock) {
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private void refusePush(User user) {
        Arrays.stream(UserPushAgreementGroupType.values())
            .forEach(group ->
                group.getAgreementTypes()
                    .forEach(agreementType -> itUtil.createUserPushAgreement(user, agreementType, false))
            );
    }

    private Push stubPushWithTargetAll() {
        final String linkUrl = someAlphanumericString(10);
        final String content = someAlphanumericString(10);
        final String title = someAlphanumericString(10);

        Push push = createReadyPush(title, content);
        push.setPushTargetType(PushTargetType.ALL);
        push.setLinkUrl(linkUrl);

        mockSendPushNotificationWithTopic(push, content);
        addShouldSendPushNotificationWithTopicToAssertions(push, content);

        return itUtil.updatePush(push);
    }

    private Push stubPushWithTargetStock() {

        final String title = someAlphanumericString(10);
        final String content = someAlphanumericString(10);
        final List<String> tokens = Stream.of(user1, user2, user3).map(User::getPushToken).toList();
        final Set<String> noTargetTokensSet = Stream.of(user4, user5)
            .map(User::getPushToken)
            .collect(Collectors.toSet());
        final String linkUrl = someAlphanumericString(10);

        final String pushNotificationContent = "[%s] %s".formatted(stock.getName(), content);

        Push push = createReadyPush(title, content);
        push.setTopic(null);
        push.setPushTargetType(PushTargetType.STOCK);
        push.setStockCode(stock.getCode());
        push.setStock(stock);
        push.setLinkUrl(linkUrl);

        mockSendPushNotificationWithTokens(push, pushNotificationContent, tokens);
        addShouldSendPushNotificationWithTokensToAssertions(push, pushNotificationContent, tokens);
        addShouldNotSendPushNotificationWithTokensToAssertions(noTargetTokensSet);

        return itUtil.updatePush(push);
    }

    private Push stubPushWithTargetStockGroup() {
        final String linkUrl = someAlphanumericString(10);

        final Stock stock1 = itUtil.createStock();
        final Stock stock2 = itUtil.createStock();
        final Stock stock3 = itUtil.createStock();
        final StockGroup stockGroup = itUtil.createStockGroup(someString(40));

        itUtil.createStockGroupMapping(stock1.getCode(), stockGroup.getId());
        itUtil.createStockGroupMapping(stock2.getCode(), stockGroup.getId());
        itUtil.createStockGroupMapping(stock3.getCode(), stockGroup.getId());

        itUtil.createUserHoldingStock(stock1.getCode(), user1);
        itUtil.createUserHoldingStock(stock2.getCode(), user2);
        itUtil.createUserHoldingStock(stock3.getCode(), user3);

        final String title = someAlphanumericString(10);
        final String content = someAlphanumericString(20);

        final List<String> tokens = Stream.of(user1, user2, user3)
            .map(User::getPushToken)
            .collect(Collectors.toSet())
            .stream().toList();

        final Set<String> noTargetTokensSet = Stream.of(user4, user5)
            .map(User::getPushToken)
            .collect(Collectors.toSet());

        Push push = createReadyPush(title, content);
        push.setTopic(null);
        push.setPushTargetType(PushTargetType.STOCK_GROUP);
        push.setStockGroupId(stockGroup.getId());
        push.setStockGroup(stockGroup);
        push.setLinkUrl(linkUrl);

        mockSendPushNotificationWithTokens(push, content, tokens);
        addShouldSendPushNotificationWithTokensToAssertions(push, content, tokens);
        addShouldNotSendPushNotificationWithTokensToAssertions(noTargetTokensSet);

        return itUtil.updatePush(push);
    }

    private Push stubPushWithAutomatedCommentPush(User user, Stock stock) {
        final Post post = itUtil.createPost(itUtil.createBoard(stock), user.getId());
        final Comment parentComment = itUtil.createComment(post.getId(), user.getId(), null, CommentType.POST, Status.ACTIVE);
        final AutomatedPushStubResult result = getAutomatedPushStubResult(user, post);

        itUtil.createAutomatedAuthorPush(
            parentComment.getId(),
            result.push().getId(),
            REPLY_COMMENT_CRITERIA_1,
            AutomatedPushCriteria.REPLY,
            AutomatedPushContentType.COMMENT
        );

        return result.updatedPush();
    }

    private Push stubPushWithAutomatedPostPush(User user, Stock stock) {
        final Post post = itUtil.createPost(itUtil.createBoard(stock), user.getId());
        final AutomatedPushStubResult result = getAutomatedPushStubResult(user, post);

        itUtil.createAutomatedAuthorPush(
            post.getId(),
            result.push().getId(),
            REPLY_COMMENT_CRITERIA_1,
            AutomatedPushCriteria.COMMENT,
            AutomatedPushContentType.POST
        );

        return result.updatedPush();
    }

    @NotNull
    private AutomatedPushStubResult getAutomatedPushStubResult(User user, Post post) {
        final String linkUrl = someAlphanumericString(10);
        final String content = someAlphanumericString(20);
        final String title = someAlphanumericString(10);

        final List<String> tokens = Stream.of(user)
            .map(User::getPushToken)
            .collect(Collectors.toSet())
            .stream().toList();

        Push push = createReadyPush(title, content);
        push.setTopic(null);
        push.setPushTargetType(PushTargetType.AUTOMATED_AUTHOR);
        push.setTargetDatetime(somePastLocalDateTimeInPushSafeTime());
        push.setPostId(post.getId());
        push.setLinkUrl(linkUrl);
        final Push updatedPush = itUtil.updatePush(push);

        mockSendPushNotificationWithTokens(push, content, tokens);
        addShouldSendPushNotificationWithTokensToAssertions(push, content, tokens);

        return new AutomatedPushStubResult(push, updatedPush);
    }

    private record AutomatedPushStubResult(Push push, Push updatedPush) {
    }

    private void addShouldNotSendPushNotificationWithTokensToAssertions(Set<String> noTargetTokensMap) {
        assertions.add(() ->
            then(firebaseMessagingService)
                .should(never())
                .sendPushNotification(
                    any(CreateFcmPushDataDto.class),
                    (List<String>) argThat(argTokens -> ((List<String>) argTokens).stream().anyMatch(noTargetTokensMap::contains))
                )
        );
    }

    private void addShouldSendPushNotificationWithTokensToAssertions(Push push, String content, List<String> tokens) {
        assertions.add(() ->
            then(firebaseMessagingService)
                .should()
                .sendPushNotification(
                    CreateFcmPushDataDto.newInstance(push.getTitle(), content, push.getLinkUrl()),
                    tokens
                )
        );
    }

    private void addShouldSendPushNotificationWithTopicToAssertions(Push push, String content) {
        assertions.add(() ->
            then(firebaseMessagingService)
                .should()
                .sendPushNotification(
                    CreateFcmPushDataDto.newInstance(push.getTitle(), content, push.getLinkUrl()),
                    PushTopic.NOTICE
                )
        );
    }

    private void mockSendPushNotificationWithTokens(Push push, String content, List<String> tokens) {
        given(firebaseMessagingService.sendPushNotification(
                CreateFcmPushDataDto.newInstance(push.getTitle(), content, push.getLinkUrl()),
                tokens
            )
        ).willReturn(Boolean.TRUE);
    }

    private void mockSendPushNotificationWithTopic(Push push, String content) {
        given(firebaseMessagingService.sendPushNotification(
                new CreateFcmPushDataDto(push.getTitle(), content, push.getLinkUrl()),
                PushTopic.NOTICE
            )
        ).willReturn(Boolean.TRUE);
    }

    private Push createReadyPush(String title, String content) {
        final String linkUrl = someAlphanumericString(10);

        Push push = itUtil.createPush(content, PushTargetType.ALL);
        push.setTitle(title);
        push.setSendStatus(PushSendStatus.READY);
        push.setTargetDatetime(somePastLocalDateTimeInPushSafeTime());
        push.setLinkUrl(linkUrl);
        return push;
    }

    private LocalDateTime somePastLocalDateTimeInPushSafeTime() {
        return LocalDateTime.now()
            .minusMinutes(someIntegerBetween(1, 59))
            .minusSeconds(someIntegerBetween(1, 59));
    }
}
