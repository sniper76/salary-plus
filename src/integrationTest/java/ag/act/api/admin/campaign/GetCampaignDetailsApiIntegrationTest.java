package ag.act.api.admin.campaign;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Campaign;
import ag.act.entity.Post;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.model.CampaignDetailsDataResponse;
import ag.act.model.CampaignDetailsResponse;
import ag.act.model.SimplePostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ag.act.TestUtil.assertTime;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class GetCampaignDetailsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/campaigns/{campaignId}";

    private String jwt;
    private Campaign campaign;
    private Post sourcePost;
    private StockGroup sourceStockGroup;
    private String givenTitle;
    private Long campaignId;
    private final List<Post> duplicatedPosts = new ArrayList<>();

    @Nested
    class FailToGetCampaignDetails {
        @Nested
        class AndCampaignNotFound {
            @BeforeEach
            void setUp() {
                itUtil.init();

                User user = itUtil.createAdminUser();
                Board board = itUtil.createBoard(itUtil.createStock());

                jwt = itUtil.createJwt(user.getId());
                sourcePost = itUtil.createPost(board, user.getId());
                sourceStockGroup = itUtil.createStockGroup(someAlphanumericString(10));
                givenTitle = someAlphanumericString(10);

                campaign = itUtil.createCampaign(
                    givenTitle,
                    sourcePost.getId(),
                    sourceStockGroup.getId()
                );

                campaignId = someLong();
            }

            @Test
            void shouldThrowBadRequest() throws Exception {
                itUtil.assertErrorResponse(callApiToFail(), 400, "캠페인이 존재하지 않습니다.");
            }
        }

        @Nested
        class AndSourceStockGroupNotFound {
            @BeforeEach
            void setUp() {
                itUtil.init();

                User user = itUtil.createAdminUser();
                Board board = itUtil.createBoard(itUtil.createStock());

                jwt = itUtil.createJwt(user.getId());
                sourcePost = itUtil.createPost(board, user.getId());
                givenTitle = someAlphanumericString(10);

                campaign = itUtil.createCampaign(
                    givenTitle,
                    sourcePost.getId(),
                    someLong()
                );

                campaignId = campaign.getId();
            }

            @Test
            void shouldThrowBadRequest() throws Exception {
                itUtil.assertErrorResponse(callApiToFail(), 400, "종목그룹이 존재하지 않습니다.");
            }
        }

        @Nested
        class AndSourcePostNotFound {
            @BeforeEach
            void setUp() {
                itUtil.init();

                User user = itUtil.createAdminUser();

                jwt = itUtil.createJwt(user.getId());
                sourceStockGroup = itUtil.createStockGroup(someAlphanumericString(10));
                givenTitle = someAlphanumericString(10);

                campaign = itUtil.createCampaign(
                    givenTitle,
                    someLong(),
                    sourceStockGroup.getId()
                );
                campaignId = campaign.getId();
            }

            @Test
            void shouldThrowBadRequest() throws Exception {
                itUtil.assertErrorResponse(callApiToFail(), 400, "캠페인의 게시글이 존재하지 않습니다.");
            }
        }
    }

    @Nested
    class WhenGetCampaignDetails {
        @BeforeEach
        void setUp() {
            itUtil.init();

            User user = itUtil.createAdminUser();
            Board board = itUtil.createBoard(itUtil.createStock());

            jwt = itUtil.createJwt(user.getId());
            sourcePost = itUtil.createPost(board, user.getId());
            sourceStockGroup = itUtil.createStockGroup(someAlphanumericString(10));
            givenTitle = someAlphanumericString(10);

            campaign = itUtil.createCampaign(
                givenTitle,
                sourcePost.getId(),
                sourceStockGroup.getId()
            );

            LongStream.range(0L, 10L).forEach(i ->
                duplicatedPosts.add(
                    itUtil.createDuplicatePost(sourcePost.getId(), board, user.getId())
                )
            );

            campaignId = campaign.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final CampaignDetailsDataResponse result = getResponse(callApi());

            assertCampaignDetailsDataResponse(result);
        }

        private void assertCampaignDetailsDataResponse(CampaignDetailsDataResponse result) {
            final CampaignDetailsResponse response = result.getData();

            assertThat(response.getId(), is(campaign.getId()));
            assertThat(response.getTitle(), is(givenTitle));
            assertThat(response.getSourcePost().getId(), is(sourcePost.getId()));
            assertThat(response.getSourceStockGroupId(), is(sourceStockGroup.getId()));
            assertThat(response.getSourceStockGroupName(), is(sourceStockGroup.getName()));
            assertThat(response.getStatus(), is(campaign.getStatus()));
            assertTime(response.getCreatedAt(), campaign.getCreatedAt());
            assertTime(response.getDeletedAt(), campaign.getDeletedAt());
            assertTime(response.getUpdatedAt(), campaign.getUpdatedAt());

            assertCampaignPosts(response);
        }

        private void assertCampaignPosts(CampaignDetailsResponse response) {
            List<SimplePostResponse> campaignPosts = response.getCampaignPosts();
            assertThat(campaignPosts.size(), is(duplicatedPosts.size() + 1));
            assertThat(campaignPosts.get(0).getPostId(), is(sourcePost.getId()));

            Map<Long, Post> postMap = duplicatedPosts.stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));

            campaignPosts.stream()
                .skip(1)
                .forEach(campaignPost -> {
                    Post matchedPost = postMap.get(campaignPost.getPostId());
                    assertThat(matchedPost, notNullValue());
                    assertThat(campaignPost.getStock().getCode(), is(matchedPost.getBoard().getStockCode()));
                    assertThat(campaignPost.getStock().getName(), is(matchedPost.getBoard().getStock().getName()));
                });
        }
    }

    private CampaignDetailsDataResponse getResponse(MvcResult response) throws Exception {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            CampaignDetailsDataResponse.class
        );
    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, campaignId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private MvcResult callApiToFail() throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, campaignId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isBadRequest())
            .andReturn();
    }
}
