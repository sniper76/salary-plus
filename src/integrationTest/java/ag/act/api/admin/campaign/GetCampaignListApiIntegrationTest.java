package ag.act.api.admin.campaign;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.Board;
import ag.act.entity.Campaign;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.enums.admin.CampaignSearchType;
import ag.act.model.CampaignResponse;
import ag.act.model.GetCampaignsDataResponse;
import ag.act.model.Paging;
import ag.act.repository.interfaces.JoinCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class GetCampaignListApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/campaigns";
    private String jwt;
    private Map<String, Object> params;
    private Integer pageNumber;
    private Campaign campaign1;
    private long campaign1StockGroupSize;
    private Campaign campaign2;
    private long campaign2StockGroupSize;
    private Campaign campaign3;
    private long campaign3StockGroupSize;
    private Campaign campaign4;
    private long campaign4StockGroupSize;
    private Campaign campaign5;
    private long campaign5StockGroupSize;
    private String givenTitle;
    private JoinCount joinCount1;
    private JoinCount joinCount2;
    private JoinCount joinCount3;
    private JoinCount joinCount4;
    private JoinCount joinCount5;

    @BeforeEach
    void setUp() {
        itUtil.init();
        jwt = itUtil.createJwt(itUtil.createAdminUser().getId());
        givenTitle = someAlphanumericString(10);

        campaign1StockGroupSize = someLongBetween(1L, 10L);
        campaign1 = createCampaign(campaign1StockGroupSize, someAlphanumericString(2) + givenTitle);
        joinCount1 = createDigitalDocumentJoinCount(campaign1);

        campaign2StockGroupSize = someLongBetween(1L, 10L);
        campaign2 = createCampaign(campaign2StockGroupSize);
        joinCount2 = createDigitalDocumentJoinCount(campaign2);

        campaign3StockGroupSize = someLongBetween(1L, 10L);
        campaign3 = createCampaign(campaign3StockGroupSize);
        joinCount3 = createDigitalDocumentJoinCount(campaign3);

        campaign4StockGroupSize = someLongBetween(1L, 10L);
        campaign4 = createCampaign(campaign4StockGroupSize);
        joinCount4 = createDigitalDocumentJoinCount(campaign4);

        campaign5StockGroupSize = someLongBetween(1L, 10L);
        campaign5 = createCampaign(campaign5StockGroupSize);
        joinCount5 = createDigitalDocumentJoinCount(campaign5);
    }

    private JoinCount createDigitalDocumentJoinCount(Campaign campaign) {
        final Long sourcePostId = campaign.getSourcePostId();
        final Post post = itUtil.findPostNoneNull(sourcePostId);
        final DigitalDocument digitalDocument = post.getDigitalDocument();

        final Integer joinCnt = someIntegerBetween(100, 1000);
        final Long stockQuantity = someLongBetween(100L, 1000L);
        digitalDocument.setJoinUserCount(joinCnt);
        digitalDocument.setJoinStockSum(stockQuantity);
        itUtil.updateDigitalDocument(digitalDocument);

        return new JoinCount() {
            @Override
            public Integer getJoinCnt() {
                return joinCnt;
            }

            @Override
            public Long getStockQuantity() {
                return stockQuantity;
            }
        };

    }

    private GetCampaignsDataResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            GetCampaignsDataResponse.class
        );
    }

    private Campaign createCampaign(long stockGroupSize, String optionalTitle) {
        return itUtil.createCampaign(
            optionalTitle == null ? someAlphanumericString(10) : optionalTitle,
            stockGroupSize,
            LocalDateTime.now().minusDays(3),
            LocalDateTime.now().plusDays(1),
            LocalDate.now()
        );
    }

    private Campaign createCampaign(long stockGroupSize) {
        return createCampaign(stockGroupSize, null);
    }

    private Map<String, Object> createParamsMap(CampaignSearchType searchType, String searchKeyword) {
        return Map.of(
            "searchType", searchType.name(),
            "searchKeyword", searchKeyword,
            "page", pageNumber.toString(),
            "size", SIZE.toString(),
            "sorts", "createdAt:desc"
        );
    }

    private void assertCampaignResponse(
        Campaign campaign, JoinCount joinCount, CampaignResponse campaignResponse, long stockGroupSize
    ) {
        assertThat(campaignResponse.getId(), is(campaign.getId()));
        assertThat(campaignResponse.getStatus(), is(campaign.getStatus()));
        assertThat(campaignResponse.getTitle(), is(campaign.getTitle()));

        final Long sourcePostId = campaign.getSourcePostId();

        assertThat(campaignResponse.getJoinStockCount(), is(joinCount.getStockQuantity()));
        assertThat(campaignResponse.getJoinUserCount(), is(joinCount.getJoinCnt()));
        assertThat(campaignResponse.getIsPoll(), is(false));
        assertThat(campaignResponse.getIsDigitalDocument(), is(true));

        assertThat(campaignResponse.getSourcePostId(), is(sourcePostId));
        assertThat(campaignResponse.getSourceStockGroupId(), is(campaign.getSourceStockGroupId()));
        assertTime(campaignResponse.getCreatedAt(), campaign.getCreatedAt());
        assertTime(campaignResponse.getUpdatedAt(), campaign.getUpdatedAt());
        assertTime(campaignResponse.getDeletedAt(), campaign.getDeletedAt());
        assertThat(campaignResponse.getMappedStocksCount(), is(stockGroupSize));

        final Post databasePost = itUtil.findPost(sourcePostId).orElseThrow();
        final Board databaseBoard = databasePost.getBoard();
        final Stock databaseStock = itUtil.findStock(databaseBoard.getStockCode());
        final StockGroup databaseStockGroup = itUtil.findStockGroupById(campaign.getSourceStockGroupId()).orElseThrow();
        assertThat(campaignResponse.getBoardCategory(), is(databaseBoard.getCategory().name()));
        assertThat(campaignResponse.getSourceStockGroupName(), is(databaseStockGroup.getName()));
        assertThat(campaignResponse.getStockQuantity(), is(databaseStock.getTotalIssuedQuantity()));
        assertThat(campaignResponse.getTargetEndDate(), is(DateTimeConverter.convert(databasePost.getDigitalDocument().getTargetEndDate())));
    }

    private void assertPaging(Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
    }

    @Nested
    class WhenSearchAllUsers {
        @Nested
        class AndFirstPage {
            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = createParamsMap(CampaignSearchType.TITLE, "");
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnCampaigns() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetCampaignsDataResponse result) {
                final List<CampaignResponse> campaignResponseList = result.getData();

                assertThat(campaignResponseList.size(), is(SIZE));
                assertCampaignResponse(campaign5, joinCount5, campaignResponseList.get(0), campaign5StockGroupSize);
                assertCampaignResponse(campaign4, joinCount4, campaignResponseList.get(1), campaign4StockGroupSize);
            }
        }

        @Nested
        class AndSecondPage {
            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = createParamsMap(CampaignSearchType.TITLE, "");
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetCampaignsDataResponse result) {
                final List<CampaignResponse> campaignResponseList = result.getData();

                assertThat(campaignResponseList.size(), is(SIZE));
                assertCampaignResponse(campaign3, joinCount3, campaignResponseList.get(0), campaign3StockGroupSize);
                assertCampaignResponse(campaign2, joinCount2, campaignResponseList.get(1), campaign2StockGroupSize);
            }
        }
    }

    @Nested
    class WhenSearchTitleKeyword {

        @Nested
        class AndFirstCampaign {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = createParamsMap(CampaignSearchType.TITLE, givenTitle);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetCampaignsDataResponse result) {
                final Paging paging = result.getPaging();
                final List<CampaignResponse> campaignResponseList = result.getData();

                assertPaging(paging, 1L);
                assertThat(campaignResponseList.size(), is(1));
                assertCampaignResponse(campaign1, joinCount1, campaignResponseList.get(0), campaign1StockGroupSize);
            }
        }

        @Nested
        class AndNotFoundCampaign {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = createParamsMap(CampaignSearchType.TITLE, someString(30));
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetCampaignsDataResponse result) {
                final Paging paging = result.getPaging();
                final List<CampaignResponse> campaignResponseList = result.getData();

                assertPaging(paging, 0L);
                assertThat(campaignResponseList.size(), is(0));
            }
        }

        @Nested
        class AndFirstCampaignForBoardCategory {
            @Nested
            class AndAll {
                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "searchType", CampaignSearchType.TITLE.name(),
                        "searchKeyword", givenTitle,
                        "boardCategory", "ALL",
                        "page", pageNumber.toString(),
                        "size", SIZE.toString(),
                        "sorts", "createdAt:desc"
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetCampaignsDataResponse result) {
                    final Paging paging = result.getPaging();
                    final List<CampaignResponse> campaignResponseList = result.getData();

                    assertPaging(paging, 1L);
                    assertThat(campaignResponseList.size(), is(1));
                    assertCampaignResponse(campaign1, joinCount1, campaignResponseList.get(0), campaign1StockGroupSize);
                }
            }

            @Nested
            class AndEtcDocument {
                @BeforeEach
                void setUp() {
                    pageNumber = PAGE_1;
                    params = Map.of(
                        "searchType", CampaignSearchType.TITLE.name(),
                        "searchKeyword", givenTitle,
                        "boardCategory", BoardCategory.ETC.name(),
                        "page", pageNumber.toString(),
                        "size", SIZE.toString(),
                        "sorts", "createdAt:desc"
                    );
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnUsers() throws Exception {
                    assertResponse(callApiAndGetResult());
                }

                private void assertResponse(GetCampaignsDataResponse result) {
                    final Paging paging = result.getPaging();
                    final List<CampaignResponse> campaignResponseList = result.getData();

                    assertPaging(paging, 1L);
                    assertThat(campaignResponseList.size(), is(1));
                    assertCampaignResponse(campaign1, joinCount1, campaignResponseList.get(0), campaign1StockGroupSize);
                }
            }
        }
    }
}
