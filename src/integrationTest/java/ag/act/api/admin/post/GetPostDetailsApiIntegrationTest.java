package ag.act.api.admin.post;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.Board;
import ag.act.entity.FileContent;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.Push;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import ag.act.enums.SelectionOption;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.model.DigitalDocumentDownloadResponse;
import ag.act.model.DigitalDocumentResponse;
import ag.act.model.LeaderElectionWinnerResponse;
import ag.act.model.PollItemResponse;
import ag.act.model.PollResponse;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.SolidarityLeaderApplicantResponse;
import ag.act.model.SolidarityLeaderElectionApplicantDataLabelResponse;
import ag.act.model.SolidarityLeaderElectionApplicantDataResponse;
import ag.act.model.SolidarityLeaderElectionDetailResponse;
import ag.act.model.SolidarityLeaderElectionResponse;
import ag.act.model.SolidarityLeaderElectionVoteItemDataResponse;
import ag.act.model.Status;
import ag.act.model.StockResponse;
import ag.act.module.solidarity.election.label.SolidarityLeaderElectionConditionalLabelDataGenerator;
import ag.act.util.ImageUtil;
import ag.act.util.KoreanDateTimeUtil;
import ag.act.util.NumberUtil;
import ag.act.util.badge.BadgeLabelGenerator;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someBoardCategory;
import static ag.act.TestUtil.someStockCode;
import static ag.act.enums.DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class GetPostDetailsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroup}/posts/{postId}";

    @Autowired
    private BadgeLabelGenerator badgeLabelGenerator;

    private String stockCode;
    private String adminJwt;
    private Board board;
    private User adminUser;
    private Stock stock;
    private Long postId;
    private Solidarity solidarity;

    @BeforeEach
    void setUp() {
        itUtil.init();
        adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());
        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), adminUser);
    }

    @Nested
    class WhenGetDetailDigitalDocument {
        private Post post;
        private String zipFileKey;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);

            String zipFilePath = someAlphanumericString(10);
            zipFileKey = someAlphanumericString(10);

            post = createPostWithDigitalDocument(adminUser, someString(10), zipFileKey, zipFilePath, board, stock);
            postId = post.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertDocumentResponse(getPostDetailsDataResponse());
        }

        private void assertDocumentResponse(PostDetailsDataResponse result) {
            final PostDetailsResponse createResponse = result.getData();

            itUtil.assertPostBoardGroupAndCategory(post, createResponse);

            assertThat(createResponse.getPoll(), is(nullValue()));
            assertThat(createResponse.getDigitalProxy(), is(nullValue()));
            assertThat(createResponse.getDigitalDocument(), is(notNullValue()));

            assertThat(createResponse.getId(), is(notNullValue()));
            assertThat(createResponse.getBoardId(), is(board.getId()));

            DigitalDocumentResponse digitalDocumentResponse = createResponse.getDigitalDocument();
            assertThat(digitalDocumentResponse, is(notNullValue()));
            assertThat(digitalDocumentResponse.getVersion(), is(DigitalDocumentVersion.V1.name()));

            DigitalDocumentDownloadResponse digitalDocumentDownloadResponse = digitalDocumentResponse.getDigitalDocumentDownload();
            assertThat(digitalDocumentDownloadResponse.getZipFileKey(), is(zipFileKey));

            assertNullLabels(createResponse);
        }
    }

    @Nested
    class WhenGetHolderListReadAndCopy {

        private FileContent fileContent;
        private DigitalDocumentUser digitalDocumentUser;
        private DigitalDocument digitalDocument;
        private Post post;
        private User leaderUser;
        private final BoardCategory boardCategory = BoardCategory.HOLDER_LIST_READ_AND_COPY;

        @BeforeEach
        void setUp() {
            leaderUser = itUtil.createUser();
            itUtil.createSolidarityLeader(solidarity, leaderUser.getId());

            board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, boardCategory);
            post = itUtil.createPost(board, leaderUser.getId());

            final PostImage postImage = itUtil.createPostImage(post.getId());
            fileContent = itUtil.getFileContent(postImage.getImageId());

            digitalDocument = itUtil.createDigitalDocument(post, stock, null, HOLDER_LIST_READ_AND_COPY_DOCUMENT);

            digitalDocumentUser = itUtil.createDigitalDocumentUser(
                digitalDocument,
                leaderUser,
                stock,
                someString(10),
                DigitalDocumentAnswerStatus.COMPLETE
            );

            digitalDocument.setDigitalDocumentUserList(List.of(digitalDocumentUser));
            post.setDigitalDocument(digitalDocument);
            itUtil.updatePost(post);
            postId = post.getId();
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            final PostDetailsDataResponse postDetailsDataResponse = getPostDetailsDataResponse();
            final PostDetailsResponse postDetailsResponse = postDetailsDataResponse.getData();

            assertResponse(postDetailsResponse);
        }

        private void assertResponse(PostDetailsResponse postDetailsResponse) {
            assertThat(postDetailsResponse.getDigitalDocument(), is(nullValue()));
            assertThat(postDetailsResponse.getUserProfile(), is(notNullValue()));
            assertThat(postDetailsResponse.getUserProfile().getNickname(), is(leaderUser.getNickname()));

            assertThat(postDetailsResponse.getHolderListReadAndCopyDigitalDocument(), is(notNullValue()));
            assertThat(postDetailsResponse.getHolderListReadAndCopyDigitalDocument().getDigitalDocumentId(), is(digitalDocument.getId()));
            assertThat(postDetailsResponse.getHolderListReadAndCopyDigitalDocument().getUserId(), is(leaderUser.getId()));

            final String fileName = FilenameUtils.getName(digitalDocumentUser.getPdfPath());
            assertThat(postDetailsResponse.getHolderListReadAndCopyDigitalDocument().getFileName(), is(fileName));

            List<ag.act.model.SimpleImageResponse> postImageList = postDetailsResponse.getPostImageList();
            assertThat(postImageList.size(), is(1));
            assertThat(postImageList.get(0).getImageId(), is(fileContent.getId()));
            assertThat(postImageList.get(0).getImageUrl(), is(ImageUtil.getFullPath(s3Environment.getBaseUrl(), fileContent.getFilename())));

        }
    }

    @Nested
    class WhenGetDetailPoll {
        private final int addItemSize = 4;
        private PollAnswer pollAnswer1;
        private PollAnswer pollAnswer2;
        private Post post;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);

            post = createPostWithPoll(adminUser, board);
            postId = post.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertDocumentResponse(getPostDetailsDataResponse());
        }

        private void assertDocumentResponse(PostDetailsDataResponse result) {
            final PostDetailsResponse createResponse = result.getData();

            itUtil.assertPostBoardGroupAndCategory(post, createResponse);

            assertThat(createResponse.getPoll(), is(notNullValue()));
            assertThat(createResponse.getDigitalProxy(), is(nullValue()));
            assertThat(createResponse.getDigitalDocument(), is(nullValue()));

            assertThat(createResponse.getId(), is(notNullValue()));
            assertThat(createResponse.getBoardId(), is(board.getId()));

            final StockResponse stockResponse = createResponse.getStock();
            assertThat(stockResponse.getTotalIssuedQuantity(), is(stock.getTotalIssuedQuantity()));
            assertThat(stockResponse.getRepresentativePhoneNumber(), is(stock.getRepresentativePhoneNumber()));
            assertThat(stockResponse.getCode(), is(stock.getCode()));

            PollResponse pollResponse = createResponse.getPoll();
            List<PollItemResponse> items = pollResponse.getPollItems();

            assertThat(items.size(), is(addItemSize));
            assertThat(pollResponse.getVoteTotalStockSum(), is(pollAnswer1.getStockQuantity() + pollAnswer2.getStockQuantity()));
            assertThat(pollResponse.getVoteTotalCount(), is(2));

            assertThat(items.stream()
                .filter(it -> Objects.equals(it.getId(), pollAnswer1.getPollItemId()))
                .findFirst()
                .orElseThrow()
                .getVoteItemCount(), is(1));
            assertThat(items.stream()
                .filter(it -> Objects.equals(it.getId(), pollAnswer2.getPollItemId()))
                .findFirst()
                .orElseThrow()
                .getVoteItemCount(), is(1));

            assertNullLabels(createResponse);
        }

        private Post createPostWithPoll(User user, Board board) {
            Post post = itUtil.createPost(board, user.getId(), false);
            LocalDateTime startDateTime = KoreanDateTimeUtil.getYesterdayStartDateTime();
            LocalDateTime endDateTime = startDateTime.plusDays(5);

            Poll poll = itUtil.createPoll(post, addItemSize, SelectionOption.SINGLE_ITEM, startDateTime, endDateTime).getFirstPoll();
            List<PollItem> pollItemList = poll.getPollItemList();

            User answerUser1 = itUtil.createUser();
            User answerUser2 = itUtil.createUser();
            pollAnswer1 = itUtil.createPollAnswer(answerUser1.getId(), poll.getId(), pollItemList.get(0).getId());
            pollAnswer2 = itUtil.createPollAnswer(answerUser2.getId(), poll.getId(), pollItemList.get(1).getId());

            return post;
        }
    }

    @Nested
    class WhenGetExclusiveToHoldersPostDetails {
        private Post exclusiveToHoldersPost;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);

            final String zipFilePath = someAlphanumericString(10);
            final String zipFileKey = someAlphanumericString(10);

            exclusiveToHoldersPost = setIsExclusiveToHoldersTrue(
                createPostWithDigitalDocument(adminUser, someString(10), zipFileKey, zipFilePath, board, stock)
            );
            postId = exclusiveToHoldersPost.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertDocumentResponse(getPostDetailsDataResponse());
        }

        private void assertDocumentResponse(PostDetailsDataResponse result) {
            final PostDetailsResponse postDetailsResponse = result.getData();

            itUtil.assertPostTitleAndContentForAdmin(exclusiveToHoldersPost, postDetailsResponse);
            itUtil.assertPostBoardGroupAndCategory(exclusiveToHoldersPost, postDetailsResponse);
        }
    }

    @Nested
    class WhenGlobalEventWithPush {
        private Push push;
        private final LocalDateTime now = LocalDateTime.now();
        private final LocalDateTime pushTargetDatetime = LocalDateTime.now();
        private LocalDateTime activeStartDate;
        private LocalDateTime activeEndDate;
        private String pushTitle;
        private String pushContent;

        @BeforeEach
        void setUp() {
            activeStartDate = now.plusDays(1);
            activeEndDate = now.plusDays(2);

            board = itUtil.createBoard(stock, BoardGroup.GLOBALEVENT, someBoardCategory(BoardGroup.GLOBALEVENT));
            Post post = itUtil.createPost(board, adminUser.getId(), Boolean.FALSE);

            post.setStatus(Status.ACTIVE);
            post.setActiveStartDate(activeStartDate);
            post.setActiveEndDate(activeEndDate);

            push = itUtil.createPush(someString(10), PushTargetType.ALL, post.getId());
            push.setTargetDatetime(pushTargetDatetime);
            push.setSendStatus(PushSendStatus.READY);
            push.setSendType(PushSendType.SCHEDULE);
            itUtil.updatePush(push);

            pushTitle = push.getTitle();
            pushContent = push.getContent();

            post.setPushId(push.getId());
            post = itUtil.updatePost(post);
            postId = post.getId();
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            final PostDetailsDataResponse postDetailsDataResponse = getPostDetailsDataResponse();

            assertResponse(postDetailsDataResponse.getData());
            assertPushResponse();
        }

        private void assertResponse(PostDetailsResponse postDetailsResponse) {
            assertThat(postDetailsResponse.getIsPush(), is(Boolean.TRUE));
            assertTime(postDetailsResponse.getActiveStartDate(), activeStartDate);
            assertTime(postDetailsResponse.getActiveEndDate(), activeEndDate);
        }

        private void assertPushResponse() {
            final Push databasePush = itUtil.findPush(push.getId()).orElseThrow();
            assertThat(databasePush.getTitle(), is(pushTitle));
            assertThat(databasePush.getContent(), is(pushContent));
            assertThat(databasePush.getSendStatus(), is(push.getSendStatus()));
            assertTime(databasePush.getTargetDatetime(), push.getTargetDatetime());
        }
    }

    //TODO: SolidarityLeaderElectionPostPollApiIntegrationTest 와 공통이어서, 추상화하여 리팩토링 할 수 있을 듯 합니다.
    @Nested
    class WhenGetDetailElectionPostPoll {

        private User candidateUser1;
        private User candidateUser2;
        private Post post;
        private Poll poll;
        private UserHoldingStock userHoldingStock1;
        private UserHoldingStock userHoldingStock2;
        private SolidarityLeaderElection solidarityLeaderElection;
        private List<SolidarityLeaderApplicant> solidarityLeaderApplicants;
        private List<User> applicantUsers;
        private List<PollAnswer> pollAnswers;
        private Long solidarityLeaderElectionId;
        private List<PollItem> pollItemList;
        private static final int index0Count = 2;
        private static final int index2Count = 0;
        private long index0Quantity = 0L;
        private static final long index2Quantity = 0L;
        private List<Optional<TestVoteItem>> testVoteItemDataList;
        private boolean isElected;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.SOLIDARITY_LEADER_ELECTION);

            final LocalDateTime localDateTime = LocalDateTime.now().minusDays(20);

            createApplicantUsers();
            createElectionAndPost(localDateTime);
            createUserAndHoldingStockAndPollAnswers();
            updateElectionStatusToFinish();

            postId = post.getId();
        }

        private void updateElectionStatusToFinish() {
            isElected = true;
            solidarityLeaderElection.setElectionStatus(SolidarityLeaderElectionStatus.FINISHED);
            solidarityLeaderElection.setTotalStockQuantity(index0Quantity);
            solidarityLeaderElection.setWinnerApplicantId(solidarityLeaderApplicants.get(0).getId());
            itUtil.updateSolidarityElection(solidarityLeaderElection);
            solidarityLeaderElection = itUtil.updateSolidarityElection(solidarityLeaderElection);
        }

        @Nested
        class WhenSuccess {

            @Test
            void shouldReturnSuccess() throws Exception {
                PostDetailsResponse response = getPostDetailsDataResponse().getData();

                itUtil.assertPostBoardGroupAndCategory(post, response);
                assertElectionResponse(response.getElection());
            }
        }

        private void createElectionAndPost(LocalDateTime localDateTime) {
            final LocalDateTime endDateTime = localDateTime.plusDays(5);

            solidarityLeaderElection = itUtil.createSolidarityElection(
                stock.getCode(),
                SolidarityLeaderElectionStatusGroup.FINISHED_EARLY_STATUS_GROUP,
                localDateTime
            );
            solidarityLeaderElectionId = solidarityLeaderElection.getId();

            solidarityLeaderApplicants = List.of(
                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(), candidateUser1.getId(), SolidarityLeaderElectionApplyStatus.COMPLETE, solidarityLeaderElectionId),
                itUtil.createSolidarityLeaderApplicant(
                    solidarity.getId(), candidateUser2.getId(), SolidarityLeaderElectionApplyStatus.COMPLETE, solidarityLeaderElectionId)
            );

            final User superAdminUser = itUtil.createSuperAdminUser();
            post = itUtil.createPost(board, superAdminUser.getId());
            post = itUtil.createPoll(post, solidarityLeaderApplicants.size() * 2, SelectionOption.MULTIPLE_ITEMS);
            poll = post.getFirstPoll();
            pollItemList = poll.getPollItemList();

            poll.setTargetStartDate(localDateTime);
            poll.setTargetEndDate(endDateTime);
            itUtil.updatePost(post);

            for (int j = 0; j < pollItemList.size(); j++) {
                itUtil.createSolidarityLeaderElectionPollItemMapping(
                    solidarityLeaderElectionId,
                    solidarityLeaderApplicants.get(j / 2).getId(),
                    pollItemList.get(j).getId(),
                    (j % 2 == 0) ? SolidarityLeaderElectionAnswerType.APPROVAL : SolidarityLeaderElectionAnswerType.REJECTION
                );
            }
            solidarityLeaderElection.setVoteStartDateTime(localDateTime);
            solidarityLeaderElection.setVoteEndDateTime(endDateTime);
            solidarityLeaderElection.setPostId(post.getId());
        }

        private void createApplicantUsers() {
            candidateUser1 = itUtil.createUser();
            candidateUser2 = itUtil.createUser();

            itUtil.createUserHoldingStock(stock.getCode(), candidateUser1);
            itUtil.createUserHoldingStock(stock.getCode(), candidateUser2);

            applicantUsers = List.of(candidateUser1, candidateUser2);
        }

        private void createUserAndHoldingStockAndPollAnswers() {
            final User user1 = itUtil.createUser();
            final User user2 = itUtil.createUser();

            userHoldingStock1 = itUtil.createUserHoldingStock(stock.getCode(), user1);
            userHoldingStock2 = itUtil.createUserHoldingStock(stock.getCode(), user2);

            index0Quantity = userHoldingStock1.getQuantity() + userHoldingStock2.getQuantity(); // 두 사람 모두 1번 지원자에게 찬성투표

            Long candidate1ApprovalPollItemIdx = pollItemList.get(0).getId();
            Long candidate2RejectionPollItemIdx = pollItemList.get(3).getId();

            pollAnswers = List.of(
                itUtil.createPollAnswer(user1.getId(), poll.getId(), candidate1ApprovalPollItemIdx, userHoldingStock1.getQuantity()),//찬성
                itUtil.createPollAnswer(user2.getId(), poll.getId(), candidate1ApprovalPollItemIdx, userHoldingStock2.getQuantity()),//찬성

                itUtil.createPollAnswer(user1.getId(), poll.getId(), candidate2RejectionPollItemIdx, userHoldingStock1.getQuantity()),//반대
                itUtil.createPollAnswer(user2.getId(), poll.getId(), candidate2RejectionPollItemIdx, userHoldingStock2.getQuantity())//반대
            );

            final long totalVoteQuantity = pollAnswers.stream().mapToLong(PollAnswer::getStockQuantity).sum() / applicantUsers.size();
            testVoteItemDataList = List.of(
                Optional.of(new TestVoteItem(index0Quantity, false, index0Count, getStockQuantityPercentage(totalVoteQuantity, index0Quantity))),
                Optional.empty(),
                Optional.of(new TestVoteItem(index2Quantity, false, index2Count, getStockQuantityPercentage(totalVoteQuantity, index2Quantity))),
                Optional.empty()
            );
        }

        private long getStockQuantityPercentage(long totalVoteQuantity, Long quantity) {
            return NumberUtil.getPercentage(quantity, totalVoteQuantity);
        }

        private void assertElectionResponse(SolidarityLeaderElectionResponse electionResponse) {
            assertWinner(electionResponse.getWinner());
            assertApplicants(electionResponse.getApplicants());
            assertElectionDetail(electionResponse.getElectionDetail());
        }

        private void assertWinner(LeaderElectionWinnerResponse winnerResponse) {
            assertThat(winnerResponse.getIsElected(), is(isElected));
            assertThat(winnerResponse.getNickname(), is(applicantUsers.get(0).getNickname()));
        }

        private void assertApplicants(List<SolidarityLeaderApplicantResponse> responses) {
            assertThat(responses.size(), is(solidarityLeaderApplicants.size()));
            assertApplicantResponse(responses.get(0), solidarityLeaderApplicants.get(0), applicantUsers.get(0), userHoldingStock1);
            assertApplicantResponse(responses.get(1), solidarityLeaderApplicants.get(1), applicantUsers.get(1), userHoldingStock2);
        }

        private void assertApplicantResponse(
            SolidarityLeaderApplicantResponse response,
            SolidarityLeaderApplicant solidarityLeaderApplicant,
            User user,
            UserHoldingStock userHoldingStock
        ) {
            final String individualStockQuantity = badgeLabelGenerator.generateStockQuantityBadge(userHoldingStock.getQuantity());

            assertThat(response.getSolidarityApplicantId(), is(solidarityLeaderApplicant.getId()));
            assertThat(response.getId(), is(user.getId()));
            assertThat(response.getNickname(), is(user.getNickname()));
            assertThat(response.getProfileImageUrl(), is(user.getProfileImageUrl()));
            assertThat(response.getIndividualStockCountLabel(), is(individualStockQuantity));
        }

        private void assertElectionDetail(SolidarityLeaderElectionDetailResponse result) {
            final List<SolidarityLeaderElectionApplicantDataResponse> responseList = result.getPollApplicants();
            final long totalQuantity = pollAnswers.stream().mapToLong(PollAnswer::getStockQuantity).sum() / responseList.size();

            assertThat(responseList.size(), is(solidarityLeaderApplicants.size()));
            assertThat(result.getSolidarityLeaderElectionId(), is(solidarityLeaderElectionId));
            assertThat(result.getStatus(), is(solidarityLeaderElection.getElectionStatus().name()));
            assertThat(result.getTotalVoterCount(), is(pollAnswers.size() / 2));

            AtomicInteger pollItemIndex = new AtomicInteger(0);

            for (int responseIndex = 0; responseIndex < responseList.size(); responseIndex++) {
                SolidarityLeaderElectionApplicantDataResponse response = responseList.get(responseIndex);
                assertThat(response.getSolidarityLeaderApplicantId(), is(solidarityLeaderApplicants.get(responseIndex).getId()));
                assertThat(response.getNickname(), is(applicantUsers.get(responseIndex).getNickname()));
                assertThat(response.getTotalVoteStockQuantity(), is(totalQuantity));

                final SolidarityLeaderElectionApplicantDataLabelResponse resolutionCondition = response.getResolutionCondition();
                assertThat(
                    resolutionCondition.getRequiredStockQuantityRatio(),
                    is("1/4")
                );
                assertThat(
                    resolutionCondition.getStockQuantity(),
                    is(solidarityLeaderElection.getTotalStockQuantity() / 4)
                );
                assertThat(
                    resolutionCondition.getColor(),
                    is(assertGetColor(responseIndex))
                );

                final SolidarityLeaderElectionApplicantDataLabelResponse finishedEarlyCondition = response.getFinishedEarlyCondition();
                assertThat(
                    finishedEarlyCondition.getStockQuantity(),
                    is(solidarityLeaderElection.getTotalStockQuantity() / 2)
                );
                assertThat(
                    finishedEarlyCondition.getColor(),
                    is(assertGetColor(responseIndex))
                );

                assertVoteItemDataList(response, pollItemIndex);
            }
        }

        private String assertGetColor(int index) {
            if (index == 0) {
                return SolidarityLeaderElectionConditionalLabelDataGenerator.COLOR_FOR_SATISFIED_CONDITION;
            } else {
                return SolidarityLeaderElectionConditionalLabelDataGenerator.COLOR_FOR_UNSATISFIED_CONDITION;
            }
        }

        private void assertVoteItemDataList(
            SolidarityLeaderElectionApplicantDataResponse response,
            AtomicInteger pollItemIndex
        ) {
            final List<SolidarityLeaderElectionVoteItemDataResponse> voteItemDataList = response.getPollItemGroups();
            for (int voteItemDataIndex = 0; voteItemDataIndex < voteItemDataList.size(); voteItemDataIndex++) {
                final SolidarityLeaderElectionVoteItemDataResponse voteItemData = voteItemDataList.get(voteItemDataIndex);
                final PollItem pollItem = pollItemList.get(pollItemIndex.get());

                testVoteItemDataList.get(pollItemIndex.get())
                    .ifPresent(testVoteItemData -> assertVoteItemData(voteItemData, testVoteItemData));

                assertThat(voteItemData.getPollItemId(), is(pollItem.getId()));
                assertThat(voteItemData.getTitle(), is(voteItemDataIndex == 0
                    ? SolidarityLeaderElectionAnswerType.APPROVAL.getDisplayName()
                    : SolidarityLeaderElectionAnswerType.REJECTION.getDisplayName()));

                pollItemIndex.incrementAndGet();
            }
        }

        private void assertVoteItemData(
            SolidarityLeaderElectionVoteItemDataResponse voteItemData,
            TestVoteItem testVoteItemData
        ) {
            assertThat(voteItemData.getStockQuantity(), is(testVoteItemData.stockQuantity()));
            assertThat(voteItemData.getIsVoted(), is(testVoteItemData.isVoted()));
            assertThat(voteItemData.getVoteCount(), is(testVoteItemData.voteCount()));
            assertThat(voteItemData.getStockQuantityPercentage(), is(testVoteItemData.stockQuantityPercentage()));
        }

        record TestVoteItem(
            long stockQuantity,
            boolean isVoted,
            int voteCount,
            long stockQuantityPercentage
        ) {
        }
    }

    private PostDetailsDataResponse getPostDetailsDataResponse() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, stockCode, board.getGroup().name(), postId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, "Bearer " + adminJwt)
                    .header(X_APP_VERSION, X_APP_VERSION_CMS)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            PostDetailsDataResponse.class
        );
    }

    private void assertNullLabels(PostDetailsResponse postDetailsResponse) {
        assertThat(postDetailsResponse.getUserProfile().getTotalAssetLabel(), nullValue());
        assertThat(postDetailsResponse.getUserProfile().getIndividualStockCountLabel(), nullValue());
    }

    private Post createPostWithDigitalDocument(User user, String title, String zipFileKey, String zipFilePath, Board board, Stock stock) {
        Post post = itUtil.createPost(board, user.getId());
        post.setTitle(someString(10) + title + someString(10));

        User acceptUser = itUtil.createUser();

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser, DigitalDocumentType.ETC_DOCUMENT);
        itUtil.createDigitalDocumentUser(digitalDocument, user, stock, someAlphanumericString(10), DigitalDocumentAnswerStatus.COMPLETE);
        DigitalDocumentDownload digitalDocumentDownload = itUtil.createDigitalDocumentDownload(digitalDocument.getId(), user.getId(), true);
        digitalDocumentDownload.setZipFilePath(zipFilePath);
        digitalDocumentDownload.setZipFileKey(zipFileKey);
        itUtil.updateDigitalDocumentDownload(digitalDocumentDownload);

        return itUtil.updatePost(post);
    }

    private Post setIsExclusiveToHoldersTrue(Post post) {
        post.setIsExclusiveToHolders(true);
        post = itUtil.updatePost(post);
        return post;
    }
}
