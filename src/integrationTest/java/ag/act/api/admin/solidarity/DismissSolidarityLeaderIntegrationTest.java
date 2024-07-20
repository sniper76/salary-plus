package ag.act.api.admin.solidarity;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.SlackChannel;
import ag.act.model.SimpleStringResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

public class DismissSolidarityLeaderIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/solidarity-leaders/{solidarityId}";
    private static final String successMessage = "[%s] 주주연대 대표(%s)가 %s에 의해 해임되었습니다.";

    private String jwt;
    private Long solidarityId;
    private User user;
    private Stock stock;
    private SolidarityLeader solidarityLeader;
    private ag.act.model.DismissSolidarityLeaderRequest request;
    private User adminLoginUser;

    @BeforeEach
    void setUp() {
        itUtil.init();
        adminLoginUser = itUtil.createAdminUser();
        stock = itUtil.createStock();
        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        jwt = itUtil.createJwt(adminLoginUser.getId());
        solidarityId = solidarity.getId();
        user = itUtil.createUser();
        solidarityLeader = itUtil.createSolidarityLeader(solidarity, user.getId());
    }

    @Nested
    class WhenDismissSolidarityLeader {

        @Nested
        class AndAcceptorUserDoesNotHaveAnyActiveDocuments extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                request = genRequest(solidarityLeader.getId());
            }

        }

        @Nested
        class AndAcceptorUserHasSomeActiveDocumentsInTheAnotherStock extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                request = genRequest(solidarityLeader.getId());

                final Stock anotherStock = itUtil.createStock();
                itUtil.createSolidarity(anotherStock.getCode());

                userHasActiveDocumentInGivenStock(user, anotherStock);
            }
        }

        @SuppressWarnings({"JUnitMalformedDeclaration", "unused"})
        class DefaultTestCases {

            @Test
            void shouldReturnSuccess() throws Exception {
                SimpleStringResponse result = getDataResponse(callApi(status().isOk()));

                assertThat(result.getStatus(), is("ok"));

                assertThat(itUtil.findSolidarityLeader(stock.getCode()).isEmpty(), is(true));
                then(slackMessageSender).should().sendSlackMessage(
                    successMessage.formatted(stock.getName(), user.getName(), adminLoginUser.getName()),
                    SlackChannel.ACT_SOLIDARITY_LEADER_ALERT
                );
            }
        }
    }

    @Nested
    class WhenNotFoundSolidarityLeader {

        @BeforeEach
        void setUp() {
            request = genRequest(someLong());
        }

        @Test
        void shouldReturnNotFoundException() throws Exception {
            final MvcResult mvcResult = callApi(status().isNotFound());

            itUtil.assertErrorResponse(mvcResult, 404, "주주대표를 찾을 수 없습니다.");
        }

    }

    @Nested
    class WhenSameSolidarityLeaderAndAcceptor {

        @BeforeEach
        void setUp() {
            request = genRequest(solidarityLeader.getId());
            itUtil.createStockAcceptorUser(stock.getCode(), user);
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            SimpleStringResponse result = getDataResponse(callApi(status().isOk()));

            assertThat(result.getStatus(), is("ok"));

            assertThat(itUtil.findSolidarityLeader(stock.getCode()).isEmpty(), is(true));
            assertThat(itUtil.findStockAcceptorUser(stock.getCode()).isPresent(), is(true));
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_API, solidarityId)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private SimpleStringResponse getDataResponse(MvcResult response) throws Exception {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            SimpleStringResponse.class
        );
    }

    private ag.act.model.DismissSolidarityLeaderRequest genRequest(Long solidarityLeaderId) {
        return new ag.act.model.DismissSolidarityLeaderRequest()
            .solidarityLeaderId(solidarityLeaderId);
    }

    private void userHasActiveDocumentInGivenStock(User user, Stock stock) {
        final Board board = itUtil.createBoard(stock);
        final Post post = itUtil.createPost(board, user.getId());
        LocalDateTime now = LocalDateTime.now();

        itUtil.createDigitalDocument(
            post,
            stock,
            user,
            DigitalDocumentType.DIGITAL_PROXY,
            now.minusDays(1L),
            now.plusDays(1L),
            now.toLocalDate()
        );
    }
}
