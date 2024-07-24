package ag.act.api.admin.campaign;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.CampaignStockMapping;
import ag.act.entity.FileContent;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.StockGroupMapping;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.SelectionOption;
import ag.act.enums.VoteType;
import ag.act.model.CampaignDetailsDataResponse;
import ag.act.model.CampaignDetailsResponse;
import ag.act.model.CreateCampaignRequest;
import ag.act.model.CreatePollItemRequest;
import ag.act.model.CreatePollRequest;
import ag.act.model.CreatePostRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.someBoardCategoryExcluding;
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

class CreateCampaignWithNotificationApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/campaigns";
    private final List<StockGroupMapping> stockGroupMappings = new ArrayList<>();
    private final Instant now = Instant.now();
    private CreateCampaignRequest request;
    private String jwt;
    private List<Long> imageIds;
    private User acceptUser;
    private String campaignTitle;
    private StockGroup stockGroup;
    private String postTitle;
    private String pollTitle;
    private BoardGroup boardGroup;
    private BoardCategory boardCategory;

    @BeforeEach
    void setUp() {
        itUtil.init();
        User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        campaignTitle = someString(35);
        postTitle = someString(40);
        pollTitle = someString(10);
        boardGroup = BoardGroup.ACTION;
        boardCategory = BoardCategory.SURVEYS;

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
        request.setContent(someAlphanumericString(300));
        request.setIsActive(Boolean.TRUE);
        request.setImageIds(imageIds);
        request.setIsNotification(Boolean.TRUE);

        request.polls(List.of(new CreatePollRequest()
            .voteType(someEnum(VoteType.class).name())
            .selectionOption(someEnum(SelectionOption.class).name())
            .targetStartDate(targetStartDate)
            .targetEndDate(targetEndDate)
            .title(pollTitle)
            .pollItems(List.of(
                new CreatePollItemRequest().text("FIRST_" + someString(10)),
                new CreatePollItemRequest().text("SECOND_" + someString(10))
            )))
        );

        return request;
    }

    private void assertDocumentResponse(CampaignDetailsDataResponse result) {
        final CampaignDetailsResponse campaignDetailsResponse = result.getData();

        assertThat(campaignDetailsResponse.getCampaignPosts(), nullValue());

        final Long campaignId = campaignDetailsResponse.getId();

        assertThat(campaignDetailsResponse.getTitle(), is(request.getTitle()));
        assertThat(campaignDetailsResponse.getSourceStockGroupId(), is(stockGroup.getId()));
        assertThat(campaignDetailsResponse.getSourceStockGroupName(), is(stockGroup.getName()));
        assertThat(campaignDetailsResponse.getSourcePost(), notNullValue());

        final Long postId = campaignDetailsResponse.getSourcePost().getId();
        final Post post = itUtil.findPost(postId).orElseThrow();

        assertThat(post.getFirstPoll(), is(notNullValue()));
        assertThat(post.getDigitalProxy(), is(nullValue()));
        assertThat(post.getDigitalDocument(), is(nullValue()));
        assertThat(post.getFirstPoll().getTitle(), is(pollTitle));

        List<Post> duplicatedPosts = itUtil.findAllDuplicatedPostIncludingSourcePost(postId);
        assertThat(duplicatedPosts.size(), is(stockGroupMappings.size()));
        duplicatedPosts.forEach(it -> {
            assertThat(it.getTitle(), is(postTitle));
            assertThat(it.getIsNotification(), is(Boolean.TRUE));
            assertThat(it.getFirstPoll().getTitle(), is(pollTitle));
        });

        final List<String> sortedStockGroupMappingStockCodes = getSortedStockGroupMappingStockCodes();
        final List<String> campaignStockMappingStockCodes = getCampaignStockMappingStockCodes(campaignId);

        assertThat(sortedStockGroupMappingStockCodes, is(campaignStockMappingStockCodes));

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
    class WhenCreateCampaignSurvey {

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

    @Nested
    class WhenFailToCreateWithWrongBoardCategory {

        @BeforeEach
        void setUp() {
            request = genRequest();
            final CreatePostRequest postRequest = request.getCreatePostRequest();
            postRequest.setBoardCategory(someBoardCategoryExcluding(BoardCategory.SURVEYS, BoardCategory.ETC).name());
            request.createPostRequest(postRequest);
        }

        @DisplayName("Should return 400 error when call " + TARGET_API + " with a wrong board category")
        @RepeatedTest(10)
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(status().isBadRequest(), request);

            itUtil.assertErrorResponse(response, 400, "캠페인 등록은 설문과 10초서명만 가능합니다.");
        }
    }
}
