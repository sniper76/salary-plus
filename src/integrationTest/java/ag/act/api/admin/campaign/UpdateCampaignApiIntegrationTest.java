package ag.act.api.admin.campaign;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.FileContent;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.StockGroupMapping;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import ag.act.enums.SelectionOption;
import ag.act.enums.VoteType;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.model.CampaignDetailsDataResponse;
import ag.act.model.CampaignDetailsResponse;
import ag.act.model.CreateCampaignRequest;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.CreatePollRequest;
import ag.act.model.CreatePostRequest;
import ag.act.model.UpdateCampaignRequest;
import ag.act.model.UpdatePollRequest;
import ag.act.model.UpdatePostRequest;
import ag.act.model.UpdatePostRequestDigitalDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.someLocalDateTimeInTheFutureDaysBetween;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UpdateCampaignApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/campaigns";
    private static final String TARGET_UPDATE_API = "/api/admin/campaigns/{campaignId}";
    private final List<StockGroupMapping> stockGroupMappings = new ArrayList<>();
    private final Instant now = Instant.now();
    private CreateCampaignRequest request;
    private String jwt;
    private List<Long> imageIds;
    private User acceptUser;
    private String campaignTitle;
    private Long campaignId;
    private StockGroup stockGroup;
    private String postTitle;
    private BoardGroup boardGroup;
    private BoardCategory boardCategory;
    private Stock stock;
    private String stockCode;
    private String stockGroupName;

    @BeforeEach
    void setUp() {
        itUtil.init();
        User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
    }

    private void mockSolidarity(String stockCode) {
        final Solidarity solidarity = itUtil.createSolidarity(stockCode);
        itUtil.createSolidarityLeader(solidarity, acceptUser.getId());
        itUtil.createStockReferenceDate(stockCode, someLocalDateTimeInTheFutureDaysBetween(5, 10).toLocalDate());
    }

    private void createStockGroupMapping(Stock stock) {
        stockGroupMappings.add(itUtil.createStockGroupMapping(stock.getCode(), stockGroup.getId()));
        itUtil.createBoard(stock, boardGroup, boardCategory);
    }

    private MvcResult callApi(ResultMatcher resultMatcher, CreateCampaignRequest request) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private MvcResult callUpdateApi(ResultMatcher resultMatcher, UpdateCampaignRequest request) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_UPDATE_API, campaignId)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenCreateCampaignSurveyAfterUpdate {
        private String pollTitle1;
        private String pollTitle2;
        private String pollTitleUpdate1;
        private String pollTitleUpdate2;
        private String campaignTitleUpdate;
        private String postTitleUpdate;
        private UpdateCampaignRequest requestUpdate;

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();
            stockGroupName = someString(30);
            stock = itUtil.createStock(stockCode);

            campaignTitle = someString(35);
            postTitle = someString(40);
            pollTitle1 = "POLL_TITLE_FIRST_" + someString(10);
            pollTitle2 = "POLL_TITLE_SECOND_" + someString(10);

            campaignTitleUpdate = someString(35);
            postTitleUpdate = someString(40);
            pollTitleUpdate1 = "POLL_UPDATE_TITLE_FIRST_" + someString(10);
            pollTitleUpdate2 = "POLL_UPDATE_TITLE_SECOND_" + someString(10);

            boardGroup = BoardGroup.ACTION;
            boardCategory = BoardCategory.SURVEYS;
            acceptUser = itUtil.createUser();
            mockSolidarity(stockCode);

            imageIds = Stream.of(itUtil.createImage(), itUtil.createImage()).map(FileContent::getId).toList();

            stockGroup = itUtil.createStockGroup(stockGroupName);
            createStockGroupMapping(stock);

            for (int i = 0; i < someIntegerBetween(5, 10); i++) {
                createStockGroupMapping(itUtil.createStock());
            }

            request = genRequest();
            requestUpdate = genRequestUpdate();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk(), request);
            final CampaignDetailsDataResponse result = itUtil.getResult(response, CampaignDetailsDataResponse.class);

            campaignId = result.getData().getId();

            final MvcResult responseUpdate = callUpdateApi(status().isOk(), requestUpdate);
            final CampaignDetailsDataResponse resultUpdate = itUtil.getResult(responseUpdate, CampaignDetailsDataResponse.class);
            assertDocumentResponse(resultUpdate);
        }

        private void assertDocumentResponse(CampaignDetailsDataResponse result) {
            final CampaignDetailsResponse campaignDetailsResponse = result.getData();

            assertThat(campaignDetailsResponse.getTitle(), is(requestUpdate.getTitle()));
            assertThat(campaignDetailsResponse.getSourceStockGroupId(), is(stockGroup.getId()));
            assertThat(campaignDetailsResponse.getSourceStockGroupName(), is(stockGroup.getName()));
            assertThat(campaignDetailsResponse.getSourcePost(), notNullValue());
            assertThat(campaignDetailsResponse.getCampaignPosts(), nullValue());

            final Long postId = campaignDetailsResponse.getSourcePost().getId();
            final Post post = itUtil.findPost(postId).orElseThrow();

            assertThat(post.getDigitalDocument(), is(nullValue()));
            assertThat(post.getDigitalProxy(), is(nullValue()));
            assertThat(post.getPolls(), is(notNullValue()));

            List<Post> duplicatedPosts = itUtil.findAllDuplicatedPostIncludingSourcePost(postId);
            assertThat(duplicatedPosts.size(), is(stockGroupMappings.size()));
            duplicatedPosts.forEach(it -> {
                assertThat(it.getTitle(), is(postTitleUpdate));
                final List<Poll> polls = it.getPolls();
                assertThat(polls.size(), is(2));
                assertThat(polls.get(0).getTitle(), is(pollTitleUpdate1));
                assertThat(polls.get(1).getTitle(), is(pollTitleUpdate2));
            });
        }

        private CreateCampaignRequest genRequest() {
            return new CreateCampaignRequest()
                .title(campaignTitle)
                .boardGroupName(boardGroup.name())
                .stockGroupId(stockGroup.getId())
                .createPostRequest(genRequestPoll());
        }

        private UpdateCampaignRequest genRequestUpdate() {
            return new UpdateCampaignRequest()
                .title(campaignTitleUpdate)
                .updatePostRequest(genRequestPollUpdate());
        }

        private CreatePostRequest genRequestPoll() {
            Instant targetStartDate = now.minus(someIntegerBetween(1, 3), ChronoUnit.DAYS);
            Instant targetEndDate = now.plus(someIntegerBetween(3, 10), ChronoUnit.DAYS);

            CreatePostRequest request = new CreatePostRequest();
            request.setBoardCategory(boardCategory.name());
            request.setTitle(postTitle);
            request.setContent(someAlphanumericString(300));
            request.setIsActive(Boolean.TRUE);
            request.setImageIds(imageIds);

            request.polls(
                List.of(
                    genCreatePollRequest(targetStartDate, targetEndDate, pollTitle1),
                    genCreatePollRequest(targetStartDate, targetEndDate, pollTitle2)
                )
            );

            return request;
        }

        private CreatePollRequest genCreatePollRequest(Instant targetStartDate, Instant targetEndDate, String pollTitle) {
            return new CreatePollRequest()
                .voteType(someEnum(VoteType.class).name())
                .selectionOption(someEnum(SelectionOption.class).name())
                .targetStartDate(targetStartDate)
                .targetEndDate(targetEndDate)
                .title(pollTitle)
                .pollItems(List.of(
                    new ag.act.model.CreatePollItemRequest().text("FIRST_" + someString(10)),
                    new ag.act.model.CreatePollItemRequest().text("SECOND_" + someString(10))
                ));
        }

        private UpdatePostRequest genRequestPollUpdate() {
            Instant targetStartDate = now.minus(someIntegerBetween(1, 3), ChronoUnit.DAYS);
            Instant targetEndDate = now.plus(someIntegerBetween(3, 10), ChronoUnit.DAYS);

            UpdatePostRequest request = new UpdatePostRequest();
            request.setTitle(postTitleUpdate);
            request.setContent(someAlphanumericString(300));

            request.polls(
                List.of(
                    genUpdatePollRequest(targetStartDate, targetEndDate, pollTitleUpdate1),
                    genUpdatePollRequest(targetStartDate, targetEndDate, pollTitleUpdate2)
                )
            );

            return request;
        }

        private UpdatePollRequest genUpdatePollRequest(Instant targetStartDate, Instant targetEndDate, String pollTitleUpdate) {
            return new UpdatePollRequest()
                .targetStartDate(targetStartDate)
                .targetEndDate(targetEndDate)
                .title(pollTitleUpdate);
        }
    }

    @Nested
    class WhenCreateCampaignEtcDigitalDocumentAfterUpdate {
        private String digitalDocumentTitle;
        private String digitalDocumentTitleUpdate;
        private String campaignTitleUpdate;
        private String postTitleUpdate;
        private UpdateCampaignRequest requestUpdate;

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();
            stockGroupName = someString(30);
            stock = itUtil.createStock(stockCode);

            campaignTitle = someString(35);
            postTitle = someString(40);
            digitalDocumentTitle = someString(10);

            campaignTitleUpdate = someString(35);
            postTitleUpdate = someString(40);
            digitalDocumentTitleUpdate = someString(10);

            boardGroup = BoardGroup.ACTION;
            boardCategory = BoardCategory.ETC;
            acceptUser = itUtil.createUser();
            mockSolidarity(stockCode);

            imageIds = Stream.of(itUtil.createImage(), itUtil.createImage()).map(FileContent::getId).toList();

            stockGroup = itUtil.createStockGroup(stockGroupName);
            createStockGroupMapping(stock);

            for (int i = 0; i < someIntegerBetween(5, 10); i++) {
                createStockGroupMapping(itUtil.createStock());
            }

            request = genRequest();
            requestUpdate = genRequestUpdate();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk(), request);
            final CampaignDetailsDataResponse result = itUtil.getResult(response, CampaignDetailsDataResponse.class);

            campaignId = result.getData().getId();

            final MvcResult responseUpdate = callUpdateApi(status().isOk(), requestUpdate);
            final CampaignDetailsDataResponse resultUpdate = itUtil.getResult(responseUpdate, CampaignDetailsDataResponse.class);
            assertDocumentResponse(resultUpdate);
        }

        private void assertDocumentResponse(CampaignDetailsDataResponse result) {
            final CampaignDetailsResponse campaignDetailsResponse = result.getData();

            assertThat(campaignDetailsResponse.getTitle(), is(requestUpdate.getTitle()));
            assertThat(campaignDetailsResponse.getSourceStockGroupId(), is(stockGroup.getId()));
            assertThat(campaignDetailsResponse.getSourceStockGroupName(), is(stockGroup.getName()));
            assertThat(campaignDetailsResponse.getSourcePost(), notNullValue());
            assertThat(campaignDetailsResponse.getCampaignPosts(), nullValue());

            final Long postId = campaignDetailsResponse.getSourcePost().getId();
            final Post post = itUtil.findPost(postId).orElseThrow();

            assertThat(post.getPolls(), empty());
            assertThat(post.getDigitalProxy(), is(nullValue()));
            assertThat(post.getDigitalDocument(), is(notNullValue()));
            assertThat(post.getDigitalDocument().getTitle(), is(digitalDocumentTitleUpdate));

            List<Post> duplicatedPosts = itUtil.findAllDuplicatedPostIncludingSourcePost(postId);
            assertThat(duplicatedPosts.size(), is(stockGroupMappings.size()));
            duplicatedPosts.forEach(it -> {
                assertThat(it.getTitle(), is(postTitleUpdate));
                assertThat(it.getDigitalDocument().getTitle(), is(digitalDocumentTitleUpdate));
            });
        }

        private CreateCampaignRequest genRequest() {
            return new CreateCampaignRequest()
                .title(campaignTitle)
                .boardGroupName(boardGroup.name())
                .stockGroupId(stockGroup.getId())
                .createPostRequest(genRequestDocument());
        }

        private UpdateCampaignRequest genRequestUpdate() {
            return new UpdateCampaignRequest()
                .title(campaignTitleUpdate)
                .updatePostRequest(genRequestDocumentUpdate());
        }

        private CreatePostRequest genRequestDocument() {
            Instant targetStartDate = now.minus(someIntegerBetween(1, 3), ChronoUnit.DAYS);
            Instant targetEndDate = now.plus(someIntegerBetween(3, 10), ChronoUnit.DAYS);

            CreatePostRequest request = new CreatePostRequest();
            request.setBoardCategory(boardCategory.name());
            request.setTitle(postTitle);
            request.setContent(someAlphanumericString(300));
            request.setIsActive(Boolean.TRUE);
            request.setImageIds(imageIds);

            request.digitalDocument(new CreateDigitalDocumentRequest()
                .type(DigitalDocumentType.ETC_DOCUMENT.name())
                .version(DigitalDocumentVersion.V1.name())
                .companyName(someString(10))
                .acceptUserId(acceptUser.getId())
                .targetStartDate(targetStartDate)
                .targetEndDate(targetEndDate)
                .title(digitalDocumentTitle)
                .content("<p>%s</p>".formatted(someAlphanumericString(50)))
                .attachOptions(new ag.act.model.JsonAttachOption()
                    .signImage(AttachOptionType.REQUIRED.name())
                    .idCardImage(someEnum(AttachOptionType.class).name())
                    .bankAccountImage(someEnum(AttachOptionType.class).name())
                    .hectoEncryptedBankAccountPdf(someEnum(AttachOptionType.class).name())
                )
            );

            return request;
        }

        private UpdatePostRequest genRequestDocumentUpdate() {
            Instant targetStartDate = now.minus(someIntegerBetween(1, 3), ChronoUnit.DAYS);
            Instant targetEndDate = now.plus(someIntegerBetween(3, 10), ChronoUnit.DAYS);

            UpdatePostRequest request = new UpdatePostRequest();
            request.setTitle(postTitleUpdate);
            request.setContent(someAlphanumericString(300));

            request.digitalDocument(new UpdatePostRequestDigitalDocument()
                .targetStartDate(targetStartDate)
                .targetEndDate(targetEndDate)
                .title(digitalDocumentTitleUpdate)
                .content("<p>%s</p>".formatted(someAlphanumericString(50)))
            );

            return request;
        }
    }
}
