package ag.act.api.admin.campaign;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Campaign;
import ag.act.entity.FileContent;
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
import ag.act.model.CreateCampaignRequest;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.CreatePollRequest;
import ag.act.model.CreatePostRequest;
import ag.act.model.Status;
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
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class DeleteCampaignApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/campaigns";
    private static final String TARGET_DELETE_API = "/api/admin/campaigns/{campaignId}";
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
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private MvcResult callDeleteApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_DELETE_API, campaignId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenCreateCampaignAfterDeleteSurvey {
        private String pollTitle;

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();
            stockGroupName = someString(30);
            stock = itUtil.createStock(stockCode);

            campaignTitle = someString(35);
            postTitle = someString(40);
            pollTitle = someString(10);

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
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk(), request);
            final CampaignDetailsDataResponse result = itUtil.getResult(response, CampaignDetailsDataResponse.class);

            campaignId = result.getData().getId();

            final MvcResult responseDelete = callDeleteApi(status().isOk());
            itUtil.getResult(responseDelete, ag.act.model.SimpleStringResponse.class);
            assertDocumentResponse();
        }

        private void assertDocumentResponse() {
            final Campaign databaseCampaign = itUtil.findCampaign(campaignId);

            assertThat(databaseCampaign.getStatus(), is(Status.DELETED));
            assertThat(databaseCampaign.getTitle(), is(campaignTitle));
            assertThat(databaseCampaign.getSourcePostId(), notNullValue());

            final Long postId = databaseCampaign.getSourcePostId();
            final List<Post> duplicatedPosts = itUtil.findAllDuplicatedPostIncludingSourcePost(postId);

            assertThat(duplicatedPosts.size(), is(stockGroupMappings.size()));
            duplicatedPosts.forEach(it -> assertThat(it.getStatus(), is(Status.DELETED)));
        }

        private CreateCampaignRequest genRequest() {
            return new CreateCampaignRequest()
                .title(campaignTitle)
                .boardGroupName(boardGroup.name())
                .stockGroupId(stockGroup.getId())
                .createPostRequest(genRequestPoll());
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

            request.polls(List.of(new CreatePollRequest()
                .voteType(someEnum(VoteType.class).name())
                .selectionOption(someEnum(SelectionOption.class).name())
                .targetStartDate(targetStartDate)
                .targetEndDate(targetEndDate)
                .title(pollTitle)
                .pollItems(List.of(
                    new ag.act.model.CreatePollItemRequest().text("FIRST_" + someString(10)),
                    new ag.act.model.CreatePollItemRequest().text("SECOND_" + someString(10))
                )))
            );

            return request;
        }
    }

    @Nested
    class WhenCreateCampaignAfterDeleteEtcDigitalDocument {
        private String digitalDocumentTitle;

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();
            stockGroupName = someString(30);
            stock = itUtil.createStock(stockCode);

            campaignTitle = someString(35);
            postTitle = someString(40);
            digitalDocumentTitle = someString(10);

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
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk(), request);
            final CampaignDetailsDataResponse result = itUtil.getResult(response, CampaignDetailsDataResponse.class);

            campaignId = result.getData().getId();

            final MvcResult responseDelete = callDeleteApi(status().isOk());
            itUtil.getResult(responseDelete, ag.act.model.SimpleStringResponse.class);
            assertDocumentResponse();
        }

        private void assertDocumentResponse() {
            final Campaign databaseCampaign = itUtil.findCampaign(campaignId);

            assertThat(databaseCampaign.getStatus(), is(Status.DELETED));
            assertThat(databaseCampaign.getTitle(), is(campaignTitle));
            assertThat(databaseCampaign.getSourcePostId(), notNullValue());

            final Long postId = databaseCampaign.getSourcePostId();
            final List<Post> duplicatedPosts = itUtil.findAllDuplicatedPostIncludingSourcePost(postId);
            assertThat(duplicatedPosts.size(), is(stockGroupMappings.size()));
            duplicatedPosts.forEach(it -> assertThat(it.getStatus(), is(Status.DELETED)));
        }

        private CreateCampaignRequest genRequest() {
            return new CreateCampaignRequest()
                .title(campaignTitle)
                .boardGroupName(boardGroup.name())
                .stockGroupId(stockGroup.getId())
                .createPostRequest(genRequestDocument());
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
    }
}
