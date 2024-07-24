package ag.act.api.admin.campaign;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.TestUtil.TestHtmlContent;
import ag.act.entity.Board;
import ag.act.entity.CampaignStockMapping;
import ag.act.entity.FileContent;
import ag.act.entity.LatestPostTimestamp;
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
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.model.CampaignDetailsDataResponse;
import ag.act.model.CampaignDetailsResponse;
import ag.act.model.CreateCampaignRequest;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.CreatePostRequest;
import ag.act.model.JsonAttachOption;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static ag.act.TestUtil.someHtmlContent;
import static ag.act.TestUtil.someLocalDateTimeInTheFutureDaysBetween;
import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CreateCampaignWithDigitalDocumentApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/campaigns";
    private final List<StockGroupMapping> stockGroupMappings = new ArrayList<>();
    private final Instant now = Instant.now();
    private CreateCampaignRequest request;
    private String jwt;
    private List<Long> imageIds;
    private User acceptUser;
    private String campaignTitle;
    private StockGroup stockGroup;
    private String digitalDocumentTitle;
    private String postTitle;
    private BoardGroup boardGroup;
    private BoardCategory boardCategory;
    private TestHtmlContent testHtmlContent;

    @BeforeEach
    void setUp() {
        itUtil.init();
        User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        campaignTitle = someString(35);
        postTitle = someString(40);
        digitalDocumentTitle = someString(10);
        boardGroup = BoardGroup.ACTION;
        boardCategory = BoardCategory.ETC;

        final String stockCode = someStockCode();
        final String stockGroupName = someString(30);
        final Stock stock = itUtil.createStock(stockCode);

        acceptUser = itUtil.createUser();
        mockSolidarity(stockCode);

        imageIds = Stream.of(itUtil.createImage(), itUtil.createImage()).map(FileContent::getId).toList();

        stockGroup = itUtil.createStockGroup(stockGroupName);
        createStockGroupMapping(stock);

        for (int i = 0; i < someIntegerBetween(5, 10); i++) {
            createStockGroupMapping(itUtil.createStock());
        }

        itUtil.deleteAllLatestPostTimestamps();
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

        testHtmlContent = someHtmlContent(Boolean.TRUE);
        request.setContent(testHtmlContent.html());
        request.setIsEd(Boolean.TRUE);
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
            .attachOptions(new JsonAttachOption()
                .signImage(AttachOptionType.REQUIRED.name())
                .idCardImage(someEnum(AttachOptionType.class).name())
                .bankAccountImage(someEnum(AttachOptionType.class).name())
                .hectoEncryptedBankAccountPdf(someEnum(AttachOptionType.class).name())
            )
        );

        return request;
    }

    private void assertDocumentResponse(CampaignDetailsDataResponse result) {
        final CampaignDetailsResponse campaignDetailsResponse = result.getData();
        final Long campaignId = campaignDetailsResponse.getId();

        assertThat(campaignDetailsResponse.getTitle(), is(request.getTitle()));
        assertThat(campaignDetailsResponse.getSourceStockGroupId(), is(stockGroup.getId()));
        assertThat(campaignDetailsResponse.getSourceStockGroupName(), is(stockGroup.getName()));
        assertThat(campaignDetailsResponse.getSourcePost(), notNullValue());
        assertThat(campaignDetailsResponse.getCampaignPosts(), nullValue());

        final Long postId = campaignDetailsResponse.getSourcePost().getId();
        final Post post = itUtil.findPost(postId).orElseThrow();
        assertLatestPostTimestamp(post);

        assertThat(post.getFirstPoll(), is(nullValue()));
        assertThat(post.getDigitalProxy(), is(nullValue()));
        assertThat(post.getDigitalDocument(), is(notNullValue()));
        assertThat(post.getDigitalDocument().getTitle(), is(digitalDocumentTitle));

        List<Post> duplicatedPosts = itUtil.findAllDuplicatedPostIncludingSourcePost(postId);
        assertThat(duplicatedPosts.size(), is(stockGroupMappings.size()));
        duplicatedPosts.forEach(duplicatedPost -> {
            assertThat(duplicatedPost.getTitle(), is(postTitle));
            assertThat(duplicatedPost.getDigitalDocument().getTitle(), is(digitalDocumentTitle));

            assertThat(duplicatedPost.getContent(), is(testHtmlContent.expectedHtml()));

            assertLatestPostTimestamp(duplicatedPost);
        });

        final List<String> sortedStockGroupMappingStockCodes = getSortedStockGroupMappingStockCodes();
        final List<String> campaignStockMappingStockCodes = getCampaignStockMappingStockCodes(campaignId);

        assertThat(sortedStockGroupMappingStockCodes, is(campaignStockMappingStockCodes));
    }

    private void assertLatestPostTimestamp(Post duplicatedPost) {
        final Board board = duplicatedPost.getBoard();
        final BoardCategory category = board.getCategory();
        final BoardGroup boardGroup = category.getBoardGroup();
        final Optional<LatestPostTimestamp> latestPostTimestamp = itUtil.findLatestPostTimestamp(board.getStockCode(), boardGroup, category);

        assertThat(latestPostTimestamp.isPresent(), is(true));
    }

    private List<String> getCampaignStockMappingStockCodes(Long campaignId) {
        return itUtil.findAllCampaignStockMappings(campaignId)
            .stream().map(CampaignStockMapping::getStockCode).toList();
    }

    private List<String> getSortedStockGroupMappingStockCodes() {
        return stockGroupMappings.stream()
            .sorted(Comparator.comparing(StockGroupMapping::getStockCode))
            .map(StockGroupMapping::getStockCode)
            .toList();
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

    @Nested
    class WhenCreateCampaignWithEtcDigitalDocument {

        @BeforeEach
        void setUp() {
            request = genRequest();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk(), request);
            final CampaignDetailsDataResponse result = itUtil.getResult(response, CampaignDetailsDataResponse.class);

            assertDocumentResponse(result);
        }
    }
}
