package ag.act.api.admin.solidarity;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.CorporateUser;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.SlackChannel;
import ag.act.model.UpdateSolidarityLeaderMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.Stream;

import static ag.act.TestUtil.someCorporateNo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class EditSolidarityLeaderMessageApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/solidarity-leaders/{solidarityId}/message";
    private static final String successMessage = "[%s] 주주연대 대표(%s)\n나도 한마디 수정\n==========\n%s";

    private String jwt;
    private Stock stock;
    private Solidarity solidarity;
    private User user;
    private Long solidarityId;
    private String message;
    private UpdateSolidarityLeaderMessageRequest request;
    private SolidarityLeader solidarityLeader;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
        stock = itUtil.createStock();
        solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
        solidarityLeader = itUtil.createSolidarityLeader(solidarity, user.getId());
        solidarityId = solidarity.getId();
        message = " " + someString(30) + " ";
        request = new UpdateSolidarityLeaderMessageRequest().message(message);
    }

    @Nested
    class Success {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API, solidarityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapperUtil.toJson(request))
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.SimpleStringResponse.class
            );

            assertThat(result.getStatus(), is("ok"));
            assertResult();
            then(slackMessageSender).should().sendSlackMessage(
                String.format(successMessage, stock.getName(), user.getName(), message),
                SlackChannel.ACT_SOLIDARITY_LEADER_ALERT);
        }

        private void assertResult() {
            SolidarityLeader solidarityLeader = itUtil.findSolidarityLeader(stock.getCode()).orElseThrow();
            assertThat(solidarityLeader.getMessage(), is(message.trim()));
        }
    }

    @Nested
    class WhenSolidarityLeaderNotExists {

        @BeforeEach
        void setup() {
            solidarityId = someLong();
        }

        @DisplayName("Should return 404 response code when call " + TARGET_API)
        @Test
        void shouldReturnNotFound() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API, solidarityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapperUtil.toJson(request))
                )
                .andExpect(status().isNotFound())
                .andReturn();

            itUtil.assertErrorResponse(response, 404, "주주대표를 찾을 수 없습니다.");
        }
    }

    @Nested
    class WhenUserIsNotAdmin {

        @BeforeEach
        void setup() {
            user = itUtil.createUser();
            jwt = itUtil.createJwt(user.getId());
            stock = itUtil.createStock();
            solidarity = itUtil.createSolidarity(stock.getCode());
            solidarityId = solidarity.getId();
            itUtil.createUserHoldingStock(stock.getCode(), user);
            itUtil.createSolidarityLeader(solidarity, user.getId());
        }

        @DisplayName("Should return 403 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API, solidarityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapperUtil.toJson(request))
                )
                .andExpect(status().isForbidden())
                .andReturn();

            itUtil.assertErrorResponse(response, 403, "인가되지 않은 접근입니다.");
        }

    }

    @Nested
    class WhenMessageIsBlank {

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @ParameterizedTest(name = "{index} => message=''{0}''")
        @MethodSource("valueProvider")
        void shouldReturnBadRequest(String message) throws Exception {
            // Given
            request = new UpdateSolidarityLeaderMessageRequest().message(message);

            // When
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API, solidarityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapperUtil.toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            // Then
            itUtil.assertErrorResponse(response, 400, "주주대표 한마디를 확인해주세요.");
        }

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of(""),
                Arguments.of(" "),
                Arguments.of("       "),
                Arguments.of((String) null)
            );
        }
    }

    @Nested
    class WhenCorporateUser {

        @BeforeEach
        void setUp() {
            final User changeLeaderUser = itUtil.createUser();
            final CorporateUser corporateUser = itUtil.createCorporateUser(someCorporateNo(), someString(10));
            corporateUser.setUserId(changeLeaderUser.getId());

            itUtil.updateCorporateUser(corporateUser);
            solidarityLeader.setUserId(changeLeaderUser.getId());
            itUtil.updateSolidarityLeader(solidarityLeader);
        }

        @DisplayName("Should return 404 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API, solidarityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapperUtil.toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "법인사업자는 주주대표 한마디를 등록할 수 없습니다.");
        }
    }
}
