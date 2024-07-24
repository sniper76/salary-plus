package ag.act.api.admin.post;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.file.UploadFilePathDto;
import ag.act.entity.Board;
import ag.act.entity.FileContent;
import ag.act.entity.LatestPostTimestamp;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.User;
import ag.act.enums.AppLinkType;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.ClientType;
import ag.act.enums.DigitalAnswerType;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import ag.act.enums.RoleType;
import ag.act.enums.SelectionOption;
import ag.act.enums.VoteType;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.CreatePollItemRequest;
import ag.act.model.CreatePollRequest;
import ag.act.model.CreatePostRequest;
import ag.act.model.DigitalDocumentResponse;
import ag.act.model.JsonAttachOption;
import ag.act.model.PollItemResponse;
import ag.act.model.PollResponse;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.PushDetailsResponse;
import ag.act.model.PushRequest;
import ag.act.model.SimpleImageResponse;
import ag.act.model.Status;
import ag.act.model.UserProfileResponse;
import ag.act.util.ImageUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.TestHtmlContent;
import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someHtmlContent;
import static ag.act.TestUtil.someHtmlContentWithImages;
import static ag.act.TestUtil.someLocalDateTimeInTheFutureDaysBetween;
import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CreatePostApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroup}/posts";

    private ag.act.model.CreatePostRequest request;
    private String stockCode;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private List<Long> imageIds;
    private List<SimpleImageResponse> simpleImageResponses;
    private StockReferenceDate stockReferenceDate;
    private User acceptUser;
    private FileContent image1;
    private FileContent image2;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);

        acceptUser = itUtil.createUser();
        itUtil.createStockAcceptorUser(stockCode, acceptUser);

        LocalDate referenceDate = someLocalDateTimeInTheFutureDaysBetween(5, 10).toLocalDate();
        stockReferenceDate = itUtil.createStockReferenceDate(stock.getCode(), referenceDate);

        image1 = itUtil.createImage();
        image2 = itUtil.createImage();
        imageIds = Stream.of(image1, image2).map(FileContent::getId).toList();
        simpleImageResponses = Stream.of(image1, image2)
            .map(fileContent -> new SimpleImageResponse()
                .imageId(fileContent.getId())
                .imageUrl(ImageUtil.getFullPath(s3Environment.getBaseUrl(), fileContent.getFilename()))
            )
            .toList();
    }

    @Nested
    class WhenCreateWithImageIds {
        private String expectedThumbnailImageUrl;

        @BeforeEach
        void setUp() {
            String imageUrl1 = itUtil.convertImageUrl(image1);
            expectedThumbnailImageUrl = itUtil.getThumbnailImageUrl(imageUrl1);
        }

        @Nested
        class WhenCreateNormal {

            @BeforeEach
            void setUp() {
                board = itUtil.createBoard(stock, BoardGroup.DEBATE, BoardCategory.DEBATE);
                user = itUtil.createUserRole(user, RoleType.ADMIN);

                request = genRequest();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk(), request);

                final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PostDetailsDataResponse.class
                );

                assertResponse(result);
            }

            private CreatePostRequest genRequest() {
                CreatePostRequest request = new CreatePostRequest();
                request.setBoardCategory(board.getCategory().name());
                request.setTitle(someString(10));
                request.setContent(someAlphanumericString(300));
                request.setIsActive(Boolean.TRUE);
                request.setImageIds(imageIds);
                request.setIsExclusiveToHolders(null);

                return request;
            }

            private void assertResponse(PostDetailsDataResponse result) {
                final PostDetailsResponse createResponse = result.getData();

                assertThat(createResponse.getPoll(), nullValue());
                assertThat(createResponse.getDigitalProxy(), nullValue());
                assertThat(createResponse.getDigitalDocument(), nullValue());

                assertThat(createResponse.getPostImageList().size(), is(simpleImageResponses.size()));
                assertThumbnailImageWithImageResizer(createResponse.getThumbnailImageUrl(), expectedThumbnailImageUrl);
                assertThat(createResponse.getId(), notNullValue());
                assertThat(createResponse.getBoardId(), is(board.getId()));
                assertThat(createResponse.getTitle(), is(request.getTitle()));
                assertThat(createResponse.getContent(), is(request.getContent()));
                assertThat(createResponse.getStatus(), is(Status.ACTIVE));
                assertThat(createResponse.getLikeCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getCommentCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getViewCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getDeletedAt(), nullValue());

                assertThat(createResponse.getUserProfile().getUserIp(), is("127.0"));
                assertThat(createResponse.getUserProfile().getIndividualStockCountLabel(), nullValue());
                assertThat(createResponse.getUserProfile().getTotalAssetLabel(), nullValue());
            }
        }

        @Nested
        class WhenCreateNormalWithHtmlContent {

            private TestHtmlContent testHtmlContent;

            @BeforeEach
            void setUp() {
                board = itUtil.createBoard(stock, BoardGroup.DEBATE, BoardCategory.DEBATE);
                user = itUtil.createUserRole(user, RoleType.ADMIN);

                final Boolean isEd = someBoolean();
                testHtmlContent = someHtmlContent(isEd);
                request = genRequest(testHtmlContent.html(), isEd);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk(), request);

                final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PostDetailsDataResponse.class
                );

                assertResponse(result);
            }

            private CreatePostRequest genRequest(String content, Boolean isEd) {
                CreatePostRequest request = new CreatePostRequest();
                request.setBoardCategory(board.getCategory().name());
                request.setTitle(someString(10));
                request.setContent(content);
                request.setIsEd(isEd);
                request.setIsActive(Boolean.TRUE);
                request.setImageIds(imageIds);
                request.setIsExclusiveToHolders(null);

                return request;
            }

            private void assertResponse(PostDetailsDataResponse result) {
                final PostDetailsResponse createResponse = result.getData();

                assertThat(createResponse.getPoll(), nullValue());
                assertThat(createResponse.getDigitalProxy(), nullValue());
                assertThat(createResponse.getDigitalDocument(), nullValue());

                assertThat(createResponse.getPostImageList().size(), is(simpleImageResponses.size()));
                assertThumbnailImageWithImageResizer(createResponse.getThumbnailImageUrl(), expectedThumbnailImageUrl);
                assertThat(createResponse.getId(), notNullValue());
                assertThat(createResponse.getBoardId(), is(board.getId()));
                assertThat(createResponse.getTitle(), is(request.getTitle()));

                assertThat(createResponse.getContent(), is(testHtmlContent.expectedHtml()));

                assertThat(createResponse.getStatus(), is(Status.ACTIVE));
                assertThat(createResponse.getLikeCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getCommentCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getViewCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getDeletedAt(), nullValue());

                assertThat(createResponse.getUserProfile().getUserIp(), is("127.0"));
                assertThat(createResponse.getUserProfile().getIndividualStockCountLabel(), nullValue());
                assertThat(createResponse.getUserProfile().getTotalAssetLabel(), nullValue());
            }
        }

        @Nested
        class WhenCreateWithPoll {

            @BeforeEach
            void setUp() {
                board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);
                user = itUtil.createUserRole(user, RoleType.ADMIN);

                request = genRequestPoll();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk(), request);

                final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PostDetailsDataResponse.class
                );

                assertPollResponse(result);
            }

            private CreatePostRequest genRequestPoll() {
                CreatePostRequest request = new CreatePostRequest();
                request.setBoardCategory(board.getCategory().name());
                request.setTitle(someString(10));
                request.setContent(someAlphanumericString(300));
                request.setIsActive(Boolean.TRUE);
                request.setImageIds(imageIds);
                request.setIsExclusiveToHolders(null);

                final Instant endDate = Instant.now().plus(Period.ofDays(5));
                final Instant startDate = endDate.minus(Period.ofDays(1));

                final CreatePollRequest poll = new CreatePollRequest()
                    .title(someString(10))
                    .targetStartDate(startDate.truncatedTo(ChronoUnit.SECONDS))
                    .targetEndDate(endDate.truncatedTo(ChronoUnit.SECONDS))
                    .selectionOption(someEnum(SelectionOption.class).name())
                    .voteType(someEnum(VoteType.class).name())
                    .pollItems(List.of(
                        new ag.act.model.CreatePollItemRequest().text("FIRST_" + someString(10)),
                        new ag.act.model.CreatePollItemRequest().text("SECOND_" + someString(10))
                    ));

                request.setPolls(List.of(poll));

                return request;
            }

            private void assertPollResponse(PostDetailsDataResponse result) {
                final PostDetailsResponse createResponse = result.getData();
                final Long postId = createResponse.getId();

                assertThat(createResponse.getPoll(), notNullValue());
                assertThat(createResponse.getDigitalProxy(), nullValue());
                assertThat(createResponse.getDigitalDocument(), nullValue());

                assertThat(createResponse.getPostImageList().size(), is(simpleImageResponses.size()));
                assertThumbnailImageWithImageResizer(createResponse.getThumbnailImageUrl(), expectedThumbnailImageUrl);
                assertThat(createResponse.getId(), notNullValue());
                assertThat(createResponse.getBoardId(), is(board.getId()));
                assertThat(createResponse.getTitle(), is(request.getTitle()));
                assertThat(createResponse.getContent(), is(request.getContent()));
                assertThat(createResponse.getStatus(), is(Status.ACTIVE));
                assertThat(createResponse.getLikeCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getCommentCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getViewCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getDeletedAt(), nullValue());

                assertThat(createResponse.getUserProfile().getUserIp(), is("127.0"));
                assertThat(createResponse.getUserProfile().getIndividualStockCountLabel(), nullValue());
                assertThat(createResponse.getUserProfile().getTotalAssetLabel(), nullValue());

                final CreatePollRequest requestPoll = request.getPolls().get(0);
                final PollResponse responsePoll = createResponse.getPoll();
                assertThat(responsePoll.getId(), notNullValue());
                assertThat(responsePoll.getTitle(), is(requestPoll.getTitle()));
                assertThat(responsePoll.getPostId(), is(postId));
                assertThat(responsePoll.getVoteType(), is(requestPoll.getVoteType()));
                assertThat(responsePoll.getSelectionOption(), is(requestPoll.getSelectionOption()));
                assertThat(responsePoll.getStatus(), is(Status.ACTIVE.name()));
                assertThat(responsePoll.getTargetStartDate(), is(requestPoll.getTargetStartDate()));
                assertThat(responsePoll.getTargetEndDate(), is(requestPoll.getTargetEndDate()));

                final List<CreatePollItemRequest> requestPollPollItems = requestPoll.getPollItems();
                final List<PollItemResponse> responsePollItems = responsePoll.getPollItems();
                for (int index = 0; index < responsePollItems.size(); index++) {
                    PollItemResponse responsePollItem = responsePollItems.get(index);
                    assertThat(responsePollItem.getId(), notNullValue());
                    assertThat(responsePollItem.getPollId(), is(responsePoll.getId()));
                    assertThat(responsePollItem.getText(), is(requestPollPollItems.get(index).getText()));
                    assertThat(responsePollItem.getStatus(), is(Status.ACTIVE.name()));
                    assertThat(responsePollItem.getCreatedAt(), notNullValue());
                    assertThat(responsePollItem.getUpdatedAt(), notNullValue());
                    assertThat(responsePollItem.getDeletedAt(), nullValue());
                }
            }
        }

        @Nested
        class WhenCreateWithDigitalDocument {

            @BeforeEach
            void setUp() {
                itUtil.deleteAllLatestPostTimestamps();

                board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
                user = itUtil.createUserRole(user, RoleType.ADMIN);

                request = genRequestDocument();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk(), request);

                final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PostDetailsDataResponse.class
                );

                assertDocumentResponse(result);
                assertLatestPostTimestamp();
            }

            private void assertLatestPostTimestamp() {
                List<LatestPostTimestamp> latestPostTimestamps = itUtil.findAllLatestPostTimestamps();

                assertThat(latestPostTimestamps.size(), is(1));

                LatestPostTimestamp latestPostTimestamp = latestPostTimestamps.get(0);
                assertThat(latestPostTimestamp.getStock().getCode(), is(stock.getCode()));
                assertThat(latestPostTimestamp.getBoardGroup(), is(board.getGroup()));
                assertThat(latestPostTimestamp.getBoardCategory(), is(board.getCategory()));
                assertThat(latestPostTimestamp.getTimestamp(), notNullValue());
            }

            private CreatePostRequest genRequestDocument() {
                CreatePostRequest request = new CreatePostRequest();
                request.setBoardCategory(board.getCategory().name());
                request.setTitle(someString(10));
                request.setContent(someAlphanumericString(300));
                request.setIsActive(Boolean.TRUE);
                request.setImageIds(imageIds);
                request.setIsExclusiveToHolders(null);

                final Instant endDate = Instant.now().plus(Period.ofDays(5));
                final Instant startDate = endDate.minus(Period.ofDays(1));

                request.digitalDocument(new CreateDigitalDocumentRequest()
                    .type(DigitalDocumentType.DIGITAL_PROXY.name())
                    .version(DigitalDocumentVersion.V1.name())
                    .companyName(someString(10))
                    .acceptUserId(acceptUser.getId())
                    .stockReferenceDateId(stockReferenceDate.getId())
                    .targetStartDate(startDate)
                    .targetEndDate(endDate)
                    .shareholderMeetingDate(startDate)
                    .shareholderMeetingType(someString(10))
                    .shareholderMeetingName(someString(10))
                    .designatedAgentNames(someString(10))
                    .title(someString(10))
                    .content(someAlphanumericString(50))
                    .attachOptions(new ag.act.model.JsonAttachOption()
                        .signImage(AttachOptionType.REQUIRED.name())
                        .idCardImage(AttachOptionType.OPTIONAL.name())
                        .bankAccountImage(AttachOptionType.OPTIONAL.name())
                        .hectoEncryptedBankAccountPdf(AttachOptionType.OPTIONAL.name())
                    )
                    .childItems(getItemList())
                );

                return request;
            }

            private List<ag.act.model.DigitalDocumentItemRequest> getItemList() {
                return List.of(new ag.act.model.DigitalDocumentItemRequest()
                    .title("제1안")
                    .content("제1안 내용")
                    .defaultSelectValue(null)
                    .leaderDescription(null)
                    .childItems(
                        List.of(
                            new ag.act.model.DigitalDocumentItemRequest()
                                .title("제1-1안")
                                .content("제1-1안 내용")
                                .defaultSelectValue(DigitalAnswerType.APPROVAL.name())
                                .leaderDescription("설명 제1-1안 내용"),
                            new ag.act.model.DigitalDocumentItemRequest()
                                .title("제1-2안")
                                .content("제1-2안 내용")
                                .defaultSelectValue(DigitalAnswerType.APPROVAL.name())
                                .leaderDescription("설명 제1-2안 내용")
                        )
                    ));
            }

            private void assertDocumentResponse(PostDetailsDataResponse result) {
                final PostDetailsResponse createResponse = result.getData();

                assertThat(createResponse.getPoll(), nullValue());
                assertThat(createResponse.getDigitalProxy(), nullValue());
                assertThat(createResponse.getDigitalDocument(), notNullValue());

                assertThat(createResponse.getPostImageList().size(), is(simpleImageResponses.size()));
                assertThumbnailImageWithImageResizer(createResponse.getThumbnailImageUrl(), expectedThumbnailImageUrl);
                assertThat(createResponse.getId(), notNullValue());
                assertThat(createResponse.getBoardId(), is(board.getId()));
                assertThat(createResponse.getTitle(), is(request.getTitle()));
                assertThat(createResponse.getContent(), is(request.getContent()));
                assertThat(createResponse.getStatus(), is(Status.ACTIVE));
                assertThat(createResponse.getLikeCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getCommentCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getViewCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getDeletedAt(), nullValue());

                final UserProfileResponse userProfileResponse = createResponse.getUserProfile();
                assertThat(userProfileResponse, notNullValue());
                assertThat(userProfileResponse.getNickname(), notNullValue());
                assertThat(userProfileResponse.getProfileImageUrl(), notNullValue());
                assertThat(userProfileResponse.getIndividualStockCountLabel(), nullValue());
                assertThat(userProfileResponse.getTotalAssetLabel(), nullValue());

                final DigitalDocumentResponse digitalDocument = createResponse.getDigitalDocument();
                assertThat(digitalDocument, notNullValue());
                assertTime(digitalDocument.getTargetEndDate(), request.getDigitalDocument().getTargetEndDate());
                assertTime(digitalDocument.getTargetStartDate(), request.getDigitalDocument().getTargetStartDate());
                assertThat(digitalDocument.getCompanyName(), is(request.getDigitalDocument().getCompanyName()));
                assertThat(digitalDocument.getShareholderMeetingName(), is(request.getDigitalDocument().getShareholderMeetingName()));
                assertThat(digitalDocument.getAcceptUserId(), is(request.getDigitalDocument().getAcceptUserId()));

                assertThat(digitalDocument.getItems().size(), is(request.getDigitalDocument().getChildItems().size()));

                assertThat(digitalDocument.getAttachOptions().getSignImage(), is(AttachOptionType.REQUIRED.name()));
                assertThat(digitalDocument.getAttachOptions().getIdCardImage(), is(AttachOptionType.OPTIONAL.name()));
                assertThat(digitalDocument.getAttachOptions().getBankAccountImage(), is(AttachOptionType.OPTIONAL.name()));
                assertThat(digitalDocument.getAttachOptions().getHectoEncryptedBankAccountPdf(), is(AttachOptionType.OPTIONAL.name()));
            }
        }

        @Nested
        class WhenCreateWithGlobalEvent {

            @BeforeEach
            void setUp() {
                itUtil.deleteAllLatestPostTimestamps();

                final BoardGroup boardGroup = BoardGroup.GLOBALEVENT;
                board = itUtil.createBoard(stock, boardGroup, boardGroup.getCategories().get(0));
                user = itUtil.createUserRole(user, RoleType.ADMIN);

                request = genRequestGlobalEvent();
            }

            @Nested
            class WhenIsPushAndHavePush {

                @Nested
                class WhenScheduledPush {

                    @BeforeEach
                    void setUp() {
                        final Instant activeStartDate = Instant.now().plus(Period.ofDays(2));
                        request.setActiveStartDate(activeStartDate);
                        request.push(new PushRequest()
                            .title(someString(10))
                            .content(someAlphanumericString(100))
                        );
                    }

                    @Test
                    void shouldReturnSuccess() throws Exception {
                        MvcResult response = callApi(status().isOk(), request);

                        final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                            response.getResponse().getContentAsString(),
                            PostDetailsDataResponse.class
                        );

                        assertPushResponse(result, request.getPush(), PushSendType.SCHEDULE);
                        assertLatestPostTimestamp();
                    }
                }
            }

            private void assertLatestPostTimestamp() {
                List<LatestPostTimestamp> latestPostTimestamps = itUtil.findAllLatestPostTimestamps();

                assertThat(latestPostTimestamps.size(), is(1));

                LatestPostTimestamp latestPostTimestamp = latestPostTimestamps.get(0);
                assertThat(latestPostTimestamp.getStock().getCode(), is(stock.getCode()));
                assertThat(latestPostTimestamp.getBoardGroup(), is(board.getGroup()));
                assertThat(latestPostTimestamp.getBoardCategory(), is(board.getCategory()));
                assertThat(latestPostTimestamp.getTimestamp(), notNullValue());
            }

            private CreatePostRequest genRequestGlobalEvent() {
                CreatePostRequest request = new CreatePostRequest();
                request.setBoardCategory(board.getCategory().name());
                request.setTitle(someString(10));
                request.setContent(someAlphanumericString(300));
                request.setIsActive(Boolean.TRUE);
                request.setImageIds(imageIds);
                request.setIsExclusiveToHolders(null);

                return request;
            }

            private void assertPushResponse(
                PostDetailsDataResponse result, PushRequest pushRequest, PushSendType pushSendType
            ) {
                final PostDetailsResponse createResponse = result.getData();

                assertThat(createResponse.getPoll(), nullValue());
                assertThat(createResponse.getDigitalProxy(), nullValue());
                assertThat(createResponse.getDigitalDocument(), nullValue());

                assertThat(createResponse.getPostImageList().size(), is(simpleImageResponses.size()));
                assertThumbnailImageWithImageResizer(createResponse.getThumbnailImageUrl(), expectedThumbnailImageUrl);
                assertThat(createResponse.getId(), notNullValue());
                assertThat(createResponse.getBoardId(), is(board.getId()));
                assertThat(createResponse.getTitle(), is(request.getTitle()));
                assertThat(createResponse.getContent(), is(request.getContent()));
                assertThat(createResponse.getStatus(), is(Status.ACTIVE));
                assertThat(createResponse.getLikeCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getCommentCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getViewCount(), anyOf(nullValue(), is(0L)));
                assertThat(createResponse.getDeletedAt(), nullValue());

                final UserProfileResponse userProfileResponse = createResponse.getUserProfile();
                assertThat(userProfileResponse, notNullValue());
                assertThat(userProfileResponse.getNickname(), notNullValue());
                assertThat(userProfileResponse.getProfileImageUrl(), notNullValue());
                assertThat(userProfileResponse.getIndividualStockCountLabel(), nullValue());
                assertThat(userProfileResponse.getTotalAssetLabel(), nullValue());

                final PushDetailsResponse pushDetailsResponse = createResponse.getPush();
                assertThat(pushDetailsResponse, notNullValue());
                assertThat(pushDetailsResponse.getTitle(), is(pushRequest.getTitle().trim()));
                assertThat(pushDetailsResponse.getContent(), is(pushRequest.getContent().trim()));
                assertThat(pushDetailsResponse.getStockTargetType(), is(PushTargetType.ALL.name()));
                assertThat(pushDetailsResponse.getLinkType(), is(AppLinkType.LINK.name()));
                assertThat(pushDetailsResponse.getSendType(), is(pushSendType.name()));
                assertThat(
                    pushDetailsResponse.getTargetDatetime(),
                    is(createResponse.getActiveStartDate().plus(10, ChronoUnit.MINUTES))
                );
            }
        }
    }

    @NotNull
    private MvcResult callApi(ResultMatcher resultMatcher, CreatePostRequest createPostRequest) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stockCode, board.getGroup().name())
                    .content(objectMapperUtil.toRequestBody(createPostRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
                    .header(X_APP_VERSION, X_APP_VERSION_CMS)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenCreateWithNoImageIds {
        private String expectedThumbnailImageUrl;
        private String content;

        private CreatePostRequest genRequest() {
            CreatePostRequest request = new CreatePostRequest();
            request.setBoardCategory(board.getCategory().name());
            request.setTitle(someString(10));
            request.setContent(content);
            request.setIsEd(Boolean.TRUE);
            request.setIsActive(Boolean.TRUE);
            request.setIsExclusiveToHolders(null);
            return request;
        }

        private void assertResponse(PostDetailsDataResponse result) {
            final PostDetailsResponse createResponse = result.getData();

            assertThat(createResponse.getPoll(), nullValue());
            assertThat(createResponse.getDigitalProxy(), nullValue());
            assertThat(createResponse.getDigitalDocument(), nullValue());

            assertThat(createResponse.getId(), notNullValue());
            assertThat(createResponse.getBoardId(), is(board.getId()));
            assertThat(createResponse.getTitle(), is(request.getTitle()));
            assertThat(createResponse.getContent(), is(request.getContent()));
            assertThat(createResponse.getStatus(), is(Status.ACTIVE));
            assertThat(createResponse.getLikeCount(), anyOf(nullValue(), is(0L)));
            assertThat(createResponse.getCommentCount(), anyOf(nullValue(), is(0L)));
            assertThat(createResponse.getViewCount(), anyOf(nullValue(), is(0L)));
            assertThat(createResponse.getDeletedAt(), nullValue());

            assertThat(createResponse.getUserProfile().getUserIp(), is("127.0"));
            assertThat(createResponse.getUserProfile().getIndividualStockCountLabel(), nullValue());
            assertThat(createResponse.getUserProfile().getTotalAssetLabel(), nullValue());

            final PostDetailsResponse postDetailsResponse = result.getData();
            assertPostFromDataBase(postDetailsResponse.getId());
        }

        @Nested
        class AndFirstImageIsActImage {

            @BeforeEach
            void setUp() {
                final String imageUrl1 = itUtil.convertImageUrl(image1);
                final String imageUrl2 = itUtil.convertImageUrl(image2);
                content = someHtmlContentWithImages(imageUrl1, imageUrl2);
                expectedThumbnailImageUrl = itUtil.getThumbnailImageUrl(imageUrl1);
            }

            @Nested
            class WhenCreateNormal {

                @BeforeEach
                void setUp() {
                    board = itUtil.createBoard(stock, BoardGroup.DEBATE, BoardCategory.DEBATE);
                    user = itUtil.createUserRole(user, RoleType.ADMIN);

                    request = genRequest();
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApi(status().isOk(), request);

                    final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        PostDetailsDataResponse.class
                    );

                    assertResponse(result);
                    assertThumbnailImageWithImageResizer(result.getData().getThumbnailImageUrl(), expectedThumbnailImageUrl);
                }
            }

            @Nested
            class WhenCreateWithPoll {

                @BeforeEach
                void setUp() {
                    board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.SURVEYS);
                    user = itUtil.createUserRole(user, RoleType.ADMIN);

                    request = genRequestPoll();
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApi(status().isOk(), request);

                    final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        PostDetailsDataResponse.class
                    );

                    assertPollResponse(result);
                }

                private CreatePostRequest genRequestPoll() {
                    CreatePostRequest request = new CreatePostRequest();
                    request.setBoardCategory(board.getCategory().name());
                    request.setTitle(someString(10));
                    request.setContent(content);
                    request.setIsEd(Boolean.TRUE);
                    request.setIsActive(Boolean.TRUE);

                    final Instant endDate = Instant.now().plus(Period.ofDays(5));
                    final Instant startDate = endDate.minus(Period.ofDays(1));

                    final CreatePollRequest poll = new CreatePollRequest()
                        .title(someString(10))
                        .targetStartDate(startDate.truncatedTo(ChronoUnit.SECONDS))
                        .targetEndDate(endDate.truncatedTo(ChronoUnit.SECONDS))
                        .selectionOption(someEnum(SelectionOption.class).name())
                        .voteType(someEnum(VoteType.class).name())
                        .pollItems(List.of(
                            new ag.act.model.CreatePollItemRequest().text("FIRST_" + someString(10)),
                            new ag.act.model.CreatePollItemRequest().text("SECOND_" + someString(10))
                        ));

                    request.setPolls(List.of(poll));

                    return request;
                }

                private void assertPollResponse(PostDetailsDataResponse result) {
                    final PostDetailsResponse postDetailsResponse = result.getData();
                    final Long postId = postDetailsResponse.getId();

                    assertThat(postDetailsResponse.getPoll(), notNullValue());
                    assertThat(postDetailsResponse.getDigitalProxy(), nullValue());
                    assertThat(postDetailsResponse.getDigitalDocument(), nullValue());

                    assertThat(postDetailsResponse.getThumbnailImageUrl(), is(expectedThumbnailImageUrl));
                    assertThat(postDetailsResponse.getId(), notNullValue());
                    assertThat(postDetailsResponse.getBoardId(), is(board.getId()));
                    assertThat(postDetailsResponse.getTitle(), is(request.getTitle()));
                    assertThat(postDetailsResponse.getContent(), is(request.getContent()));
                    assertThat(postDetailsResponse.getStatus(), is(Status.ACTIVE));
                    assertThat(postDetailsResponse.getLikeCount(), anyOf(nullValue(), is(0L)));
                    assertThat(postDetailsResponse.getCommentCount(), anyOf(nullValue(), is(0L)));
                    assertThat(postDetailsResponse.getViewCount(), anyOf(nullValue(), is(0L)));
                    assertThat(postDetailsResponse.getDeletedAt(), nullValue());

                    assertThat(postDetailsResponse.getUserProfile().getUserIp(), is("127.0"));
                    assertThat(postDetailsResponse.getUserProfile().getIndividualStockCountLabel(), nullValue());
                    assertThat(postDetailsResponse.getUserProfile().getTotalAssetLabel(), nullValue());

                    final CreatePollRequest requestPoll = request.getPolls().get(0);
                    final PollResponse responsePoll = postDetailsResponse.getPoll();
                    assertThat(responsePoll.getId(), notNullValue());
                    assertThat(responsePoll.getTitle(), is(requestPoll.getTitle()));
                    assertThat(responsePoll.getPostId(), is(postId));
                    assertThat(responsePoll.getVoteType(), is(requestPoll.getVoteType()));
                    assertThat(responsePoll.getSelectionOption(), is(requestPoll.getSelectionOption()));
                    assertThat(responsePoll.getStatus(), is(Status.ACTIVE.name()));
                    assertThat(responsePoll.getTargetStartDate(), is(requestPoll.getTargetStartDate()));
                    assertThat(responsePoll.getTargetEndDate(), is(requestPoll.getTargetEndDate()));

                    final List<CreatePollItemRequest> requestPollPollItems = requestPoll.getPollItems();
                    final List<PollItemResponse> responsePollItems = responsePoll.getPollItems();
                    for (int index = 0; index < responsePollItems.size(); index++) {
                        PollItemResponse responsePollItem = responsePollItems.get(index);
                        assertThat(responsePollItem.getId(), notNullValue());
                        assertThat(responsePollItem.getPollId(), is(responsePoll.getId()));
                        assertThat(responsePollItem.getText(), is(requestPollPollItems.get(index).getText()));
                        assertThat(responsePollItem.getStatus(), is(Status.ACTIVE.name()));
                        assertThat(responsePollItem.getCreatedAt(), notNullValue());
                        assertThat(responsePollItem.getUpdatedAt(), notNullValue());
                        assertThat(responsePollItem.getDeletedAt(), nullValue());
                    }
                    assertPostFromDataBase(postDetailsResponse.getId());
                }
            }

            @Nested
            class WhenCreateWithDigitalDocument {

                @BeforeEach
                void setUp() {
                    itUtil.deleteAllLatestPostTimestamps();

                    board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
                    user = itUtil.createUserRole(user, RoleType.ADMIN);

                    request = genRequestDocument();
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApi(status().isOk(), request);

                    final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        PostDetailsDataResponse.class
                    );

                    assertDocumentResponse(result);
                    assertLatestPostTimestamp();
                }

                private void assertLatestPostTimestamp() {
                    List<LatestPostTimestamp> latestPostTimestamps = itUtil.findAllLatestPostTimestamps();

                    assertThat(latestPostTimestamps.size(), is(1));

                    LatestPostTimestamp latestPostTimestamp = latestPostTimestamps.get(0);
                    assertThat(latestPostTimestamp.getStock().getCode(), is(stock.getCode()));
                    assertThat(latestPostTimestamp.getBoardGroup(), is(board.getGroup()));
                    assertThat(latestPostTimestamp.getBoardCategory(), is(board.getCategory()));
                    assertThat(latestPostTimestamp.getTimestamp(), notNullValue());
                }

                private CreatePostRequest genRequestDocument() {
                    CreatePostRequest request = new CreatePostRequest();
                    request.setBoardCategory(board.getCategory().name());
                    request.setTitle(someString(10));
                    request.setContent(content);
                    request.setIsEd(Boolean.TRUE);
                    request.setIsActive(Boolean.TRUE);

                    final Instant endDate = Instant.now().plus(Period.ofDays(5));
                    final Instant startDate = endDate.minus(Period.ofDays(1));

                    request.digitalDocument(new CreateDigitalDocumentRequest()
                        .type(DigitalDocumentType.DIGITAL_PROXY.name())
                        .version(DigitalDocumentVersion.V1.name())
                        .companyName(someString(10))
                        .acceptUserId(acceptUser.getId())
                        .stockReferenceDateId(stockReferenceDate.getId())
                        .targetStartDate(startDate)
                        .targetEndDate(endDate)
                        .shareholderMeetingDate(startDate)
                        .shareholderMeetingType(someString(10))
                        .shareholderMeetingName(someString(10))
                        .designatedAgentNames(someString(10))
                        .title(someString(10))
                        .content(someAlphanumericString(50))
                        .attachOptions(new ag.act.model.JsonAttachOption()
                            .signImage(AttachOptionType.REQUIRED.name())
                            .idCardImage(AttachOptionType.OPTIONAL.name())
                            .bankAccountImage(AttachOptionType.OPTIONAL.name())
                            .hectoEncryptedBankAccountPdf(AttachOptionType.OPTIONAL.name())
                        )
                        .childItems(getItemList())
                    );

                    return request;
                }

                private List<ag.act.model.DigitalDocumentItemRequest> getItemList() {
                    return List.of(new ag.act.model.DigitalDocumentItemRequest()
                        .title("제1안")
                        .content("제1안 내용")
                        .defaultSelectValue(null)
                        .leaderDescription(null)
                        .childItems(
                            List.of(
                                new ag.act.model.DigitalDocumentItemRequest()
                                    .title("제1-1안")
                                    .content("제1-1안 내용")
                                    .defaultSelectValue(DigitalAnswerType.APPROVAL.name())
                                    .leaderDescription("설명 제1-1안 내용"),
                                new ag.act.model.DigitalDocumentItemRequest()
                                    .title("제1-2안")
                                    .content("제1-2안 내용")
                                    .defaultSelectValue(DigitalAnswerType.APPROVAL.name())
                                    .leaderDescription("설명 제1-2안 내용")
                            )
                        ));
                }

                private void assertDocumentResponse(PostDetailsDataResponse result) {
                    final PostDetailsResponse postDetailsResponse = result.getData();

                    assertThat(postDetailsResponse.getPoll(), nullValue());
                    assertThat(postDetailsResponse.getDigitalProxy(), nullValue());
                    assertThat(postDetailsResponse.getDigitalDocument(), notNullValue());

                    assertThat(postDetailsResponse.getThumbnailImageUrl(), is(expectedThumbnailImageUrl));
                    assertThat(postDetailsResponse.getId(), notNullValue());
                    assertThat(postDetailsResponse.getBoardId(), is(board.getId()));
                    assertThat(postDetailsResponse.getTitle(), is(request.getTitle()));
                    assertThat(postDetailsResponse.getContent(), is(request.getContent()));
                    assertThat(postDetailsResponse.getStatus(), is(Status.ACTIVE));
                    assertThat(postDetailsResponse.getLikeCount(), anyOf(nullValue(), is(0L)));
                    assertThat(postDetailsResponse.getCommentCount(), anyOf(nullValue(), is(0L)));
                    assertThat(postDetailsResponse.getViewCount(), anyOf(nullValue(), is(0L)));
                    assertThat(postDetailsResponse.getDeletedAt(), nullValue());

                    final UserProfileResponse userProfileResponse = postDetailsResponse.getUserProfile();
                    assertThat(userProfileResponse, notNullValue());
                    assertThat(userProfileResponse.getNickname(), notNullValue());
                    assertThat(userProfileResponse.getProfileImageUrl(), notNullValue());
                    assertThat(userProfileResponse.getIndividualStockCountLabel(), nullValue());
                    assertThat(userProfileResponse.getTotalAssetLabel(), nullValue());

                    final DigitalDocumentResponse digitalDocument = postDetailsResponse.getDigitalDocument();
                    assertThat(digitalDocument, notNullValue());
                    assertTime(digitalDocument.getTargetEndDate(), request.getDigitalDocument().getTargetEndDate());
                    assertTime(digitalDocument.getTargetStartDate(), request.getDigitalDocument().getTargetStartDate());
                    assertThat(digitalDocument.getCompanyName(), is(request.getDigitalDocument().getCompanyName()));
                    assertThat(digitalDocument.getShareholderMeetingName(), is(request.getDigitalDocument().getShareholderMeetingName()));
                    assertThat(digitalDocument.getAcceptUserId(), is(request.getDigitalDocument().getAcceptUserId()));

                    assertThat(digitalDocument.getItems().size(), is(request.getDigitalDocument().getChildItems().size()));

                    assertThat(digitalDocument.getAttachOptions().getSignImage(), is(AttachOptionType.REQUIRED.name()));
                    assertThat(digitalDocument.getAttachOptions().getIdCardImage(), is(AttachOptionType.OPTIONAL.name()));
                    assertThat(digitalDocument.getAttachOptions().getBankAccountImage(), is(AttachOptionType.OPTIONAL.name()));
                    assertThat(digitalDocument.getAttachOptions().getHectoEncryptedBankAccountPdf(), is(AttachOptionType.OPTIONAL.name()));

                    assertPostFromDataBase(postDetailsResponse.getId());
                }
            }
        }

        @Nested
        class AndFirstImageIsNotActImage {

            @BeforeEach
            void setUp() {
                final String imageUrl1 = "https://www.google.com/test/test.jpg";
                final String imageUrl2 = itUtil.convertImageUrl(image2);
                content = someHtmlContentWithImages(imageUrl1, imageUrl2);
                expectedThumbnailImageUrl = imageUrl1;
            }

            @Nested
            class WhenCreateNormal {

                @BeforeEach
                void setUp() {
                    board = itUtil.createBoard(stock, BoardGroup.DEBATE, BoardCategory.DEBATE);
                    user = itUtil.createUserRole(user, RoleType.ADMIN);

                    request = genRequest();
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApi(status().isOk(), request);

                    final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        PostDetailsDataResponse.class
                    );

                    assertResponse(result);
                    assertThumbnailImageWithoutImageResizer(result.getData().getThumbnailImageUrl(), expectedThumbnailImageUrl);
                }
            }
        }
    }

    @Nested
    class WhenBoardCategoryIsLeaderElection {

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.SOLIDARITY_LEADER_ELECTION);
            user = itUtil.createUserRole(user, RoleType.ADMIN);

            request = new CreatePostRequest();
            request.setBoardCategory(board.getCategory().name());
            request.setTitle(someString(10));
            request.setContent(someAlphanumericString(300));
            request.setIsActive(Boolean.TRUE);
            request.setIsExclusiveToHolders(null);
        }

        @Test
        void shouldError() throws Exception {
            MvcResult response = callApi(status().isBadRequest(), request);
            itUtil.assertErrorResponse(response, 400, "대표선출 카테고리는 등록할 수 없습니다.");
        }
    }

    @Nested
    class WhenBoardCategoryIsHolderListReadAndCopy {

        private final BoardCategory boardCategory = BoardCategory.HOLDER_LIST_READ_AND_COPY;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ACTION, boardCategory);
            user = itUtil.createUserRole(user, RoleType.ADMIN);

            request = new CreatePostRequest();
            request.setBoardCategory(board.getCategory().name());
            request.setTitle(someString(10));
            request.setContent(someAlphanumericString(300));
            request.setIsActive(Boolean.TRUE);
            request.setDigitalDocument(
                new CreateDigitalDocumentRequest()
                    .type(DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT.name())
                    .companyName(someString(10))
                    .attachOptions(
                        new JsonAttachOption()
                            .signImage(AttachOptionType.NONE.name())
                            .idCardImage(AttachOptionType.NONE.name())
                            .bankAccountImage(AttachOptionType.NONE.name())
                            .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name())
                    )
                    .targetStartDate(Instant.now())
                    .targetEndDate(Instant.now().plus(Period.ofDays(5)))
            );
        }

        @Test
        void shouldReturnBadRequest() throws Exception {
            MvcResult response = callApi(status().isBadRequest(), request);
            itUtil.assertErrorResponse(response, 400, "%s 카테고리는 CMS에서 등록할 수 없습니다.".formatted(boardCategory.getDisplayName()));
        }
    }

    private void assertPostFromDataBase(Long databasePostId) {
        final Post databasePost = itUtil.findPost(databasePostId).orElseThrow();
        assertIsExclusiveToHoldersWithDefaultValue(databasePost);
        assertPostClientType(databasePost);
    }

    private void assertPostClientType(Post databasePost) {
        assertThat(databasePost.getClientType(), is(ClientType.CMS));
    }

    private void assertIsExclusiveToHoldersWithDefaultValue(Post databasePost) {
        assertThat(databasePost.getIsExclusiveToHolders(), is(Boolean.TRUE));
    }

    private void assertThumbnailImageWithoutImageResizer(String actualThumbnailImageUrl, String expectedThumbnailImageUrl) {
        assertThat(actualThumbnailImageUrl, is(expectedThumbnailImageUrl));

        then(imageIOWrapper).should(never()).read(any(InputStream.class));
        then(imageIOWrapper).should(never()).write(any(BufferedImage.class), anyString());
        then(scalrClient).should(never()).resize(any(), anyInt());
        then(itUtil.getS3ServiceMockBean()).should(never()).putObject(any(UploadFilePathDto.class), any(InputStream.class));
        then(itUtil.getS3ServiceMockBean()).should(never()).findObjectFromPublicBucket(anyString());
    }

    private void assertThumbnailImageWithImageResizer(String actualThumbnailImageUrl, String expectedThumbnailImageUrl) {
        assertThat(actualThumbnailImageUrl, is(expectedThumbnailImageUrl));

        then(imageIOWrapper).should().read(any(InputStream.class));
        then(imageIOWrapper).should(atLeastOnce()).write(any(BufferedImage.class), anyString());
        then(scalrClient).should().resize(any(), anyInt());
        then(itUtil.getS3ServiceMockBean()).should().putObject(any(UploadFilePathDto.class), any(InputStream.class));
        then(itUtil.getS3ServiceMockBean()).should().findObjectFromPublicBucket(anyString());
    }
}
