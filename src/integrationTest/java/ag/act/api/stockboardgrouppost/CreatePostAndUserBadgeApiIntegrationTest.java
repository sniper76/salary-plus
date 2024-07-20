package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.holder.web.WebBrowserDetector;
import ag.act.core.holder.web.WebBrowserDetectorFactory;
import ag.act.entity.Board;
import ag.act.entity.FileContent;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserBadgeVisibility;
import ag.act.entity.UserHoldingStock;
import ag.act.enums.AppPreferenceType;
import ag.act.enums.BoardGroup;
import ag.act.enums.ClientType;
import ag.act.enums.UserBadgeType;
import ag.act.model.CreatePostRequest;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.SimpleImageResponse;
import ag.act.model.UserProfileResponse;
import ag.act.util.ImageUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class CreatePostAndUserBadgeApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";

    private CreatePostRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private List<Long> imageIds;
    private List<SimpleImageResponse> simpleImageResponses;
    private UserHoldingStock userHoldingStock;
    private Solidarity solidarity;
    private String expectedThumbnailImageUrl;
    private String appVersion;

    @MockBean
    private WebBrowserDetectorFactory webBrowserDetectorFactory;

    @BeforeEach
    void setUp() {
        itUtil.init();
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock, BoardGroup.DEBATE, BoardGroup.DEBATE.getCategories().get(0));
        solidarity = itUtil.createSolidarity(stock.getCode());

        final FileContent image1 = itUtil.createImage();
        final FileContent image2 = itUtil.createImage();
        imageIds = Stream.of(image1, image2).map(FileContent::getId).toList();
        simpleImageResponses = Stream.of(image1, image2)
            .map(fileContent -> new SimpleImageResponse()
                .imageId(fileContent.getId())
                .imageUrl(ImageUtil.getFullPath(s3Environment.getBaseUrl(), fileContent.getFilename()))
            )
            .toList();
        String imageUrl1 = itUtil.convertImageUrl(image1);
        expectedThumbnailImageUrl = itUtil.getThumbnailImageUrl(imageUrl1);

        appVersion = someThing(AppPreferenceType.MIN_APP_VERSION.getDefaultValue(), X_APP_VERSION_WEB);
        setUpWebBrowserDetector(appVersion);
    }

    private void setUpWebBrowserDetector(String appVersion) {
        WebBrowserDetector webBrowserDetector = mock(WebBrowserDetector.class);
        given(webBrowserDetectorFactory.createWebBrowserDetector(any(HttpServletRequest.class)))
            .willReturn(webBrowserDetector);
        given(webBrowserDetector.isRequestFromWebBrowser()).willReturn(appVersion.equals(X_APP_VERSION_WEB));
    }

    private CreatePostRequest genRequest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setBoardCategory(board.getCategory().name());
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsActive(Boolean.TRUE);
        request.setIsAnonymous(Boolean.FALSE);
        request.setImageIds(imageIds);

        return request;
    }

    @NotNull
    private MvcResult callApiWithJwt(CreatePostRequest request, String jwt) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
                    .header(X_APP_VERSION, appVersion)
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private void assertResponse(PostDetailsDataResponse result) {
        final PostDetailsResponse postDetailsResponse = result.getData();

        assertThat(postDetailsResponse.getId(), notNullValue());
        assertThat(postDetailsResponse.getBoardId(), is(board.getId()));
        assertThat(postDetailsResponse.getTitle(), is(request.getTitle()));
        assertThat(postDetailsResponse.getContent(), is(request.getContent()));
        assertThat(postDetailsResponse.getPostImageList().size(), is(simpleImageResponses.size()));
        assertThat(postDetailsResponse.getThumbnailImageUrl(), is(expectedThumbnailImageUrl));
        assertThat(postDetailsResponse.getActiveStartDate(), notNullValue());

        final UserProfileResponse createResponseUserProfile = postDetailsResponse.getUserProfile();
        assertThat(createResponseUserProfile.getNickname(), is(user.getNickname()));

        assertFromDatabase(postDetailsResponse.getId());
    }

    private void assertFromDatabase(Long postId) {
        Post post = itUtil.findPost(postId).orElseThrow();

        if (appVersion.equals(X_APP_VERSION_WEB)) {
            assertThat(post.getClientType(), is(ClientType.WEB));
        } else {
            assertThat(post.getClientType(), is(ClientType.APP));
        }
    }

    @Nested
    class WhenCreatePost {
        private Long stockQuantity = 50000L;

        @NotNull
        private UserBadgeVisibility getUserBadgeVisibility(List<UserBadgeVisibility> userBadgeVisibilities, UserBadgeType type) {
            return userBadgeVisibilities
                .stream()
                .filter(it -> it.getType() == type)
                .findFirst().orElseThrow();
        }

        @Nested
        class AndNormal {
            @Nested
            class ByNotSolidarityLeader {

                @BeforeEach
                void setUp() {
                    user = itUtil.createUser();
                    jwt = itUtil.createJwt(user.getId());
                    userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
                    request = genRequest();
                    stockQuantity = 100L;
                    userHoldingStock.setQuantity(stockQuantity);
                    itUtil.updateUserHoldingStock(userHoldingStock);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApiWithJwt(request, jwt);

                    final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        PostDetailsDataResponse.class
                    );

                    assertResponse(result);
                    final UserProfileResponse userProfileResponse = result.getData().getUserProfile();
                    assertThat(userProfileResponse.getIndividualStockCountLabel(), is("100주+"));
                    assertThat(userProfileResponse.getTotalAssetLabel(), nullValue());
                    assertThat(userProfileResponse.getIsSolidarityLeader(), is(false));
                }
            }

            @Nested
            class BySolidarityLeader {

                @Nested
                class AndIsAnonymous {
                    @BeforeEach
                    void setUp() {
                        user = itUtil.createUser();
                        user.setNickname("익명");
                        jwt = itUtil.createJwt(user.getId());
                        userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
                        request = genRequest();
                        stockQuantity = 100L;
                        userHoldingStock.setQuantity(stockQuantity);
                        itUtil.updateUserHoldingStock(userHoldingStock);
                        request.setIsAnonymous(Boolean.TRUE);
                        itUtil.createSolidarityLeader(solidarity, user.getId());
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        MvcResult response = callApiWithJwt(request, jwt);

                        final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                            response.getResponse().getContentAsString(),
                            PostDetailsDataResponse.class
                        );

                        assertResponse(result);
                        final UserProfileResponse userProfileResponse = result.getData().getUserProfile();
                        assertThat(userProfileResponse.getIndividualStockCountLabel(), is(nullValue()));
                        assertThat(userProfileResponse.getTotalAssetLabel(), nullValue());
                        assertThat(userProfileResponse.getIsSolidarityLeader(), is(false));
                    }
                }

                @Nested
                class AndIsAdmin {
                    @BeforeEach
                    void setUp() {
                        user = itUtil.createAdminUser();
                        jwt = itUtil.createJwt(user.getId());
                        userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
                        request = genRequest();
                        stockQuantity = 100L;
                        userHoldingStock.setQuantity(stockQuantity);
                        itUtil.updateUserHoldingStock(userHoldingStock);
                        itUtil.createSolidarityLeader(solidarity, user.getId());
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        MvcResult response = callApiWithJwt(request, jwt);

                        final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                            response.getResponse().getContentAsString(),
                            PostDetailsDataResponse.class
                        );

                        assertResponse(result);
                        final UserProfileResponse userProfileResponse = result.getData().getUserProfile();
                        assertThat(userProfileResponse.getIndividualStockCountLabel(), is(nullValue()));
                        assertThat(userProfileResponse.getTotalAssetLabel(), nullValue());
                        assertThat(userProfileResponse.getIsSolidarityLeader(), is(false));
                    }
                }

                @Nested
                class AndIsNotAnonymousAndNotAdmin {
                    private Stock leadingStock1;
                    private Stock leadingStock2;
                    private Stock leadingStock3;

                    @BeforeEach
                    void setUp() {
                        user = itUtil.createUser();
                        jwt = itUtil.createJwt(user.getId());
                        userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
                        request = genRequest();
                        stockQuantity = 100L;
                        userHoldingStock.setQuantity(stockQuantity);
                        itUtil.updateUserHoldingStock(userHoldingStock);
                        itUtil.createSolidarityLeader(solidarity, user.getId());
                        leadingStock1 = createSolidarityLeader(user);
                        leadingStock2 = createSolidarityLeader(user);
                        leadingStock3 = createSolidarityLeader(user);
                    }

                    private Stock createSolidarityLeader(User user) {
                        final Stock stock1 = itUtil.createStock();
                        final Solidarity solidarity1 = itUtil.createSolidarity(stock1.getCode());
                        itUtil.createSolidarityLeader(solidarity1, user.getId());
                        return stock1;
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        MvcResult response = callApiWithJwt(request, jwt);

                        final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                            response.getResponse().getContentAsString(),
                            PostDetailsDataResponse.class
                        );

                        assertResponse(result);
                        final UserProfileResponse userProfileResponse = result.getData().getUserProfile();
                        assertThat(userProfileResponse.getIndividualStockCountLabel(), is("100주+"));
                        assertThat(userProfileResponse.getTotalAssetLabel(), nullValue());
                        assertThat(userProfileResponse.getIsSolidarityLeader(), is(true));
                        final List<String> leadingStockCodes = userProfileResponse
                            .getLeadingStocks().stream()
                            .map(ag.act.model.SimpleStockResponse::getCode)
                            .toList();
                        assertThat(
                            leadingStockCodes,
                            contains(stock.getCode(), leadingStock1.getCode(), leadingStock2.getCode(), leadingStock3.getCode())
                        );
                    }
                }
            }
        }

        @Nested
        class AndUserBadgeVisibility {

            @BeforeEach
            void setUp() {
                user = itUtil.createUser();
                jwt = itUtil.createJwt(user.getId());
                userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
                request = genRequest();
                itUtil.createUserBadgeVisibility(user.getId());
                userHoldingStock.setQuantity(stockQuantity);
                itUtil.updateUserHoldingStock(userHoldingStock);
                final Integer closingPrice = 50000;
                stock.setClosingPrice(closingPrice);
                itUtil.updateStock(stock);
            }

            @Nested
            class AndNotVisibleTotalAsset {

                @BeforeEach
                void setUp() {
                    final List<UserBadgeVisibility> userBadgeVisibilities = itUtil.findAllUserBadgeVisibilityByUserId(user.getId());
                    final UserBadgeVisibility userBadgeVisibilityTotalAsset = getUserBadgeVisibility(
                        userBadgeVisibilities, UserBadgeType.TOTAL_ASSET);

                    userBadgeVisibilityTotalAsset.setIsVisible(Boolean.FALSE);
                    itUtil.updateUserBadgeVisibility(userBadgeVisibilityTotalAsset);

                    final UserBadgeVisibility userBadgeVisibilityStockQuantity = getUserBadgeVisibility(
                        userBadgeVisibilities, UserBadgeType.STOCK_QUANTITY);

                    userBadgeVisibilityStockQuantity.setIsVisible(Boolean.TRUE);
                    itUtil.updateUserBadgeVisibility(userBadgeVisibilityStockQuantity);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApiWithJwt(request, jwt);

                    final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        PostDetailsDataResponse.class
                    );

                    assertResponse(result);
                    final PostDetailsResponse postDetailsResponse = result.getData();
                    assertThat(postDetailsResponse.getUserProfile().getIndividualStockCountLabel(), is("5만주+"));
                    assertThat(postDetailsResponse.getUserProfile().getTotalAssetLabel(), nullValue());
                }
            }

            @Nested
            class AndNotVisibleIndividualStockStockQuantity {

                @BeforeEach
                void setUp() {
                    final List<UserBadgeVisibility> userBadgeVisibilities = itUtil.findAllUserBadgeVisibilityByUserId(user.getId());
                    final UserBadgeVisibility userBadgeVisibilityTotalAsset = getUserBadgeVisibility(
                        userBadgeVisibilities, UserBadgeType.TOTAL_ASSET);

                    userBadgeVisibilityTotalAsset.setIsVisible(Boolean.TRUE);
                    itUtil.updateUserBadgeVisibility(userBadgeVisibilityTotalAsset);

                    final UserBadgeVisibility userBadgeVisibilityStockQuantity = getUserBadgeVisibility(
                        userBadgeVisibilities, UserBadgeType.STOCK_QUANTITY);

                    userBadgeVisibilityStockQuantity.setIsVisible(Boolean.FALSE);
                    itUtil.updateUserBadgeVisibility(userBadgeVisibilityStockQuantity);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApiWithJwt(request, jwt);

                    final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        PostDetailsDataResponse.class
                    );

                    assertResponse(result);
                    final PostDetailsResponse postDetailsResponse = result.getData();
                    assertThat(postDetailsResponse.getUserProfile().getIndividualStockCountLabel(), nullValue());
                    assertThat(postDetailsResponse.getUserProfile().getTotalAssetLabel(), is("10억+"));
                }
            }
        }
    }
}
