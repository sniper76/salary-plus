package ag.act.api.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BlockedUser;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.admin.BlockedUserFilterType;
import ag.act.model.BlockedUserResponse;
import ag.act.model.GetBlockedUserResponse;
import ag.act.model.Paging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetBlockedUsersApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/me/blocked-users";
    private static final Long TOTAL_ELEMENTS = 4L;

    private User user;
    private String jwt;
    private Map<String, Object> params;
    private Integer pageNumber;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private BlockedUser blockedUser1;
    private BlockedUser blockedUser2;
    private BlockedUser blockedUser3;
    private BlockedUser blockedUser4;
    private Stock stock1;
    private Stock stock2;
    private Stock stock3;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        final Long userId = user.getId();
        jwt = itUtil.createJwt(userId);

        user1 = itUtil.createUser();
        user2 = itUtil.createUser(); // solidarity1,2 leader
        user3 = itUtil.createUser();
        user4 = itUtil.createUser(); // solidarity3 leader

        stock1 = itUtil.createStock();
        stock2 = itUtil.createStock();
        stock3 = itUtil.createStock();
        final Solidarity solidarity1 = itUtil.createSolidarity(stock1.getCode());
        final Solidarity solidarity2 = itUtil.createSolidarity(stock2.getCode());
        final Solidarity solidarity3 = itUtil.createSolidarity(stock3.getCode());
        itUtil.createSolidarityLeader(solidarity1, user2.getId());
        itUtil.createSolidarityLeader(solidarity2, user2.getId());
        itUtil.createSolidarityLeader(solidarity3, user4.getId());

        blockedUser1 = itUtil.createBlockedUser(userId, user1.getId());
        blockedUser2 = itUtil.createBlockedUser(userId, user2.getId());
        blockedUser3 = itUtil.createBlockedUser(userId, user3.getId());
        blockedUser4 = itUtil.createBlockedUser(userId, user4.getId());
    }

    @Nested
    class WhenGetAllBlockedUsers {

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "blockedUserType", BlockedUserFilterType.ALL.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetBlockedUserResponse result) {
                final List<BlockedUserResponse> blockedUserResponses = result.getData();

                assertThat(blockedUserResponses.size(), is(SIZE));
                assertPaging(result.getPaging(), TOTAL_ELEMENTS, getSortsOrDefault(params));
                assertBlockedUserResponse(
                    blockedUserResponses.get(0),
                    blockedUser1,
                    user1,
                    Boolean.FALSE,
                    List.of()
                );
                assertBlockedUserResponse(
                    blockedUserResponses.get(1),
                    blockedUser2,
                    user2,
                    Boolean.TRUE,
                    List.of(stock1.getName(), stock2.getName())
                );
            }
        }

        @Nested
        class AndSecondPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = Map.of(
                    "blockedUserType", BlockedUserFilterType.ALL.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetBlockedUserResponse result) {
                final List<BlockedUserResponse> blockedUserResponses = result.getData();

                assertThat(blockedUserResponses.size(), is(SIZE));
                assertPaging(result.getPaging(), TOTAL_ELEMENTS, getSortsOrDefault(params));
                assertBlockedUserResponse(
                    blockedUserResponses.get(0), blockedUser3, user3, Boolean.FALSE, List.of());
                assertBlockedUserResponse(
                    blockedUserResponses.get(1), blockedUser4, user4, Boolean.TRUE, List.of(stock3.getName()));
            }
        }
    }

    @Nested
    class WhenGetBlockedNormalUsers {

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "blockedUserType", BlockedUserFilterType.NORMAL_USER.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetBlockedUserResponse result) {
                final List<BlockedUserResponse> blockedUserResponses = result.getData();

                assertThat(blockedUserResponses.size(), is(SIZE));
                assertPaging(result.getPaging(), 2, getSortsOrDefault(params));
                assertBlockedUserResponse(
                    blockedUserResponses.get(0),
                    blockedUser1,
                    user1,
                    Boolean.FALSE,
                    List.of()
                );
                assertBlockedUserResponse(
                    blockedUserResponses.get(1),
                    blockedUser3,
                    user3,
                    Boolean.FALSE,
                    List.of()
                );
            }
        }
    }

    @Nested
    class WhenGetBlockedSolidarityLeaders {

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "blockedUserType", BlockedUserFilterType.SOLIDARITY_LEADER.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetBlockedUserResponse result) {
                final List<BlockedUserResponse> blockedUserResponses = result.getData();

                assertThat(blockedUserResponses.size(), is(SIZE));
                assertPaging(result.getPaging(), 2, getSortsOrDefault(params));
                assertBlockedUserResponse(
                    blockedUserResponses.get(0),
                    blockedUser2,
                    user2,
                    Boolean.TRUE,
                    List.of(stock1.getName(), stock2.getName())
                );
                assertBlockedUserResponse(
                    blockedUserResponses.get(1),
                    blockedUser4,
                    user4,
                    Boolean.TRUE,
                    List.of(stock3.getName())
                );
            }
        }
    }


    private GetBlockedUserResponse callApiAndGetResult() throws Exception {
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
            GetBlockedUserResponse.class
        );
    }

    private void assertBlockedUserResponse(
        BlockedUserResponse blockedUserResponse,
        BlockedUser blockedUser,
        User user,
        Boolean isSolidarityLeader,
        List<String> leadingSolidarityStockNames
    ) {
        assertThat(blockedUserResponse.getId(), is(blockedUser.getId()));
        assertThat(blockedUserResponse.getBlockedUserId(), is(user.getId()));
        assertThat(blockedUserResponse.getNickname(), is(user.getNickname()));
        assertThat(blockedUserResponse.getProfileImageUrl(), is(user.getProfileImageUrl()));
        assertThat(blockedUserResponse.getIsSolidarityLeader(), is(isSolidarityLeader));
        assertThat(blockedUserResponse.getLeadingSolidarityStockNames(), is(leadingSolidarityStockNames));
        assertTime(blockedUserResponse.getCreatedAt(), blockedUser.getCreatedAt());
    }

    private void assertPaging(Paging paging, long totalElements, Object sorts) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts(), is(sorts));
    }

    private Object getSortsOrDefault(Map<String, Object> params) {
        return params.getOrDefault("sorts", List.of("createdAt:ASC"));
    }
}
