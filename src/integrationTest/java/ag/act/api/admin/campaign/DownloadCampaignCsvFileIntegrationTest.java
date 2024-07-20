package ag.act.api.admin.campaign;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Campaign;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentItemUserAnswer;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.SelectionOption;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class DownloadCampaignCsvFileIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/campaigns/{campaignId}/csv-download";

    private LocalDate referenceDate;
    private String jwt;
    private User adminUser;
    private User user1;
    private User user2;
    private User user3;
    private Long campaignId;
    private Stock stock1;
    private Stock stock2;
    private Board board1;
    private Board board2;

    @BeforeEach
    void setUp() {
        itUtil.init();

        user1 = itUtil.createUserWithAddress();
        user2 = itUtil.createUserWithAddress();
        user3 = itUtil.createUserWithAddress();
        adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
    }

    private String callApiAndGetResult() throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(
                post(TARGET_API, campaignId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return mvcResult.getResponse().getContentAsString();
    }


    private Post createPost(Long sourcePostId, Board board, User adminUser) {
        final Post post2 = itUtil.createPost(board, adminUser.getId());
        post2.setSourcePostId(sourcePostId);
        return itUtil.updatePost(post2);
    }

    @Nested
    class WhenCampaignDigitalDocumentCsv {
        private List<DigitalDocumentTestDataRecord> testDataRecords;

        @BeforeEach
        void setUp() {
            referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

            final LocalDateTime targetStartDate = LocalDateTime.now().minusDays(5);
            final LocalDateTime targetEndDate = LocalDateTime.now().minusDays(1);
            final Campaign campaign = createCampaign(targetStartDate, targetEndDate);
            campaignId = campaign.getId();
            final Long sourcePostId = campaign.getSourcePostId();
            final Post post2 = createPost(sourcePostId, board2, adminUser);

            final DigitalDocument digitalDocument1 = itUtil.findDigitalDocumentByPostId(sourcePostId);
            final DigitalDocument digitalDocument2 = createDigitalDocument(post2, targetStartDate, targetEndDate);
            final List<DigitalDocumentItem> digitalDocumentItemList1 = itUtil.createDigitalDocumentItemList(digitalDocument1);
            final List<DigitalDocumentItem> digitalDocumentItemList2 = itUtil.createDigitalDocumentItemList(digitalDocument2);

            createMyDataSummaryForUsers(stock1, referenceDate);
            createMyDataSummaryForUsers(stock2, referenceDate);

            testDataRecords = List.of(
                createTestDataRecord(user1, digitalDocument1, stock1, digitalDocumentItemList1),
                createTestDataRecord(user2, digitalDocument1, stock1, digitalDocumentItemList1),
                createTestDataRecord(user3, digitalDocument1, stock1, digitalDocumentItemList1),
                createTestDataRecord(user1, digitalDocument2, stock2, digitalDocumentItemList2),
                createTestDataRecord(user2, digitalDocument2, stock2, digitalDocumentItemList2),
                createTestDataRecord(user3, digitalDocument2, stock2, digitalDocumentItemList2)
            );
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldDownloadAllData() throws Exception {
            final String actual = callApiAndGetResult();
            final String expectedResult = getExpectedResult();

            assertThat(actual, is(expectedResult));
        }

        private Campaign createCampaign(LocalDateTime targetStartDate, LocalDateTime targetEndDate) {
            final StockGroup stockGroup = itUtil.createStockGroup(someString(10));

            stock1 = itUtil.createStock(someStockCode());
            stock2 = itUtil.createStock(someStockCode());

            itUtil.createStockGroupMapping(stock1.getCode(), stockGroup.getId());
            itUtil.createStockGroupMapping(stock2.getCode(), stockGroup.getId());

            board2 = itUtil.createBoard(stock2);

            return itUtil.createCampaign(
                someAlphanumericString(10), List.of(stock1, stock2), targetStartDate, targetEndDate, referenceDate
            );
        }


        private DigitalDocumentTestDataRecord createTestDataRecord(
            User user, DigitalDocument digitalDocument, Stock stock, List<DigitalDocumentItem> digitalDocumentItemList
        ) {
            return new DigitalDocumentTestDataRecord(
                user,
                createDigitalDocumentUser(user, digitalDocument, stock),
                itUtil.createDigitalDocumentItemUserAnswerList(user.getId(), digitalDocumentItemList)
            );
        }

        private void createMyDataSummaryForUsers(Stock stock, LocalDate referenceDate) {
            itUtil.createMyDataSummary(user1, stock.getCode(), referenceDate);
            itUtil.createMyDataSummary(user2, stock.getCode(), referenceDate);
            itUtil.createMyDataSummary(user3, stock.getCode(), referenceDate);
        }


        private DigitalDocument createDigitalDocument(Post post2, LocalDateTime targetStartDate, LocalDateTime targetEndDate) {
            return itUtil.createDigitalDocument(
                post2,
                stock2,
                createAcceptUserAsLeader(stock2),
                DigitalDocumentType.DIGITAL_PROXY,
                targetStartDate,
                targetEndDate,
                referenceDate
            );
        }

        private DigitalDocumentUser createDigitalDocumentUser(User user, DigitalDocument document, Stock stock) {
            final String pdfPath = "%s/%s".formatted(user.getId(), someString(10));
            itUtil.createUserHoldingStock(stock.getCode(), user);
            return itUtil.createDigitalDocumentUser(document, user, stock, pdfPath, DigitalDocumentAnswerStatus.COMPLETE);
        }

        private User createAcceptUserAsLeader(Stock stock) {
            final User acceptUser = itUtil.createUser();
            final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
            itUtil.createSolidarityLeader(solidarity, acceptUser.getId());

            return acceptUser;
        }

        private String getExpectedResult() {
            final String expectedUserResponse = testDataRecords.stream()
                .map(this::getExpectedResult)
                .collect(Collectors.joining("\n"));

            return """
                번호,이름,생년월일,성별,주소,상세주소,우편번호,전화번호,주식명,주식수,평균매수가,차입금,작성일시,가입일자,제1-1안,제1-2안
                %s
                """.formatted(expectedUserResponse);
        }

        private String getExpectedResult(DigitalDocumentTestDataRecord testDataRecord) {
            return itUtil.getExpectedCsvRecord(
                testDataRecord.user(),
                testDataRecord.digitalDocumentUser(),
                testDataRecord.userAnswerList()
            );
        }

        private record DigitalDocumentTestDataRecord(
            User user, DigitalDocumentUser digitalDocumentUser, List<DigitalDocumentItemUserAnswer> userAnswerList
        ) {

        }
    }

    @Nested
    class WhenCampaignPollCsv {
        private List<PollTestDataRecord> testDataRecords;
        private Post sourcePost;

        @SuppressWarnings("LineLength")
        @BeforeEach
        void setUp() {
            stock1 = itUtil.createStock(someStockCode());
            board1 = itUtil.createBoard(stock1, BoardGroup.ACTION, BoardCategory.SURVEYS);
            stock2 = itUtil.createStock(someStockCode());
            board2 = itUtil.createBoard(stock2, BoardGroup.ACTION, BoardCategory.SURVEYS);

            sourcePost = itUtil.createPost(board1, adminUser.getId());
            Poll sourcePostPoll = itUtil.createPoll(sourcePost, 2, SelectionOption.SINGLE_ITEM).getFirstPoll();

            Post duplicatePost = itUtil.createDuplicatePostWithPoll(sourcePost, board2);
            Poll duplicatePoll = duplicatePost.getFirstPoll();

            List<PollItem> sourcePostPollItems = sourcePostPoll.getPollItemList();
            List<PollItem> duplicatePollItems = duplicatePoll.getPollItemList();

            Campaign campaign = createCampaign();
            campaignId = campaign.getId();

            testDataRecords = List.of(
                createPollTestDataRecord(stock1, user1, sourcePost, sourcePostPollItems, List.of(createPollAnswer(user1, sourcePostPoll, sourcePostPollItems.get(0)))),
                createPollTestDataRecord(stock1, user2, sourcePost, sourcePostPollItems, List.of(createPollAnswer(user2, sourcePostPoll, sourcePostPollItems.get(1)))),
                createPollTestDataRecord(stock1, user3, sourcePost, sourcePostPollItems, List.of(createPollAnswer(user3, sourcePostPoll, sourcePostPollItems.get(0)))),
                createPollTestDataRecord(stock2, user1, duplicatePost, duplicatePollItems, List.of(createPollAnswer(user1, duplicatePoll, duplicatePollItems.get(1)))),
                createPollTestDataRecord(stock2, user2, duplicatePost, duplicatePollItems, List.of(createPollAnswer(user2, duplicatePoll, duplicatePollItems.get(0))))
            );
        }

        @Test
        void shouldDownloadAllData() throws Exception {
            final String actual = callApiAndGetResult();
            final String expectedResult = getExpectedResult();

            assertThat(actual, is(expectedResult));
        }

        private Campaign createCampaign() {
            final StockGroup stockGroup = itUtil.createStockGroup(someString(10));

            itUtil.createStockGroupMapping(stock1.getCode(), stockGroup.getId());
            itUtil.createStockGroupMapping(stock2.getCode(), stockGroup.getId());

            return itUtil.createCampaign(
                someAlphanumericString(10), sourcePost.getId(), stockGroup.getId()
            );
        }

        private PollAnswer createPollAnswer(User user, Poll poll, PollItem pollItem) {
            return itUtil.createPollAnswer(user.getId(), poll.getId(), pollItem.getId());
        }

        private PollTestDataRecord createPollTestDataRecord(
            Stock stock, User user, Post post, List<PollItem> pollItemList, List<PollAnswer> userAnswerList
        ) {
            return new PollTestDataRecord(
                stock, user, post, pollItemList, userAnswerList
            );
        }

        private String getExpectedResult() {
            final List<PollItem> pollItemList = sourcePost.getFirstPoll().getPollItemList();
            final String firstPollItemText = pollItemList.get(0).getText();
            final String secondPollItemText = pollItemList.get(1).getText();

            final String expectedUserResponse = testDataRecords.stream()
                .map(this::getExpectedCsvRecord)
                .collect(Collectors.joining("\n"));

            return """
                유저아이디,유저이름,종목코드,종목명,게시물번호,보유주식수,%s-주주수,%s-주식수,%s-주주수,%s-주식수
                %s
                """.formatted(firstPollItemText, firstPollItemText, secondPollItemText, secondPollItemText, expectedUserResponse);
        }

        private String getExpectedCsvRecord(PollTestDataRecord pollTestDataRecord) {
            Stock stock = pollTestDataRecord.stock;
            User user = pollTestDataRecord.user;
            Post post = pollTestDataRecord.post;
            List<PollItem> pollItemList = pollTestDataRecord.pollItemList;
            List<PollAnswer> userAnswerList = pollTestDataRecord.userAnswerList;

            Long stockQuantity = userAnswerList.get(0).getStockQuantity();

            Stream<String> defaultData = Stream.of(
                user.getId().toString(),
                user.getName(),
                stock.getCode(),
                stock.getName(),
                post.getId().toString(),
                userAnswerList.get(0).getStockQuantity().toString()
            );

            Stream<String> userAnswerData = pollItemList.stream()
                .flatMap(pollItem -> {
                    boolean inAnswerList = userAnswerList.stream()
                        .anyMatch(pollAnswer -> pollAnswer.getPollItemId().equals(pollItem.getId()));

                    return inAnswerList
                        ? Stream.of("1", stockQuantity.toString())
                        : Stream.of("0", "0");
                });

            return String.join(
                ",",
                Stream.concat(defaultData, userAnswerData).toList()
            );
        }

        private record PollTestDataRecord(
            Stock stock, User user, Post post, List<PollItem> pollItemList, List<PollAnswer> userAnswerList
        ) {

        }
    }
}
