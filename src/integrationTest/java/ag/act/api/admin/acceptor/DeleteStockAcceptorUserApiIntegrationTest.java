package ag.act.api.admin.acceptor;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockAcceptorUser;
import ag.act.entity.StockAcceptorUserHistory;
import ag.act.entity.User;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.RoleType;
import ag.act.model.DeleteStockAcceptorUserRequest;
import ag.act.model.SimpleStringResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeleteStockAcceptorUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/acceptor-users";

    private Stock stock;
    private User user;
    private User adminUser;
    private DeleteStockAcceptorUserRequest request;
    private String adminJwt;

    @BeforeEach
    void setUp() {
        adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());

        stock = itUtil.createStock();
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_API, stock.getCode())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(adminJwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenSuccess {

        @BeforeEach
        void setUp() {
            user = itUtil.createUser();
            itUtil.createUserRole(user, RoleType.ACCEPTOR_USER);
            itUtil.createStockAcceptorUser(stock.getCode(), user);
            request = new DeleteStockAcceptorUserRequest().userId(user.getId());
        }

        private void assertResponse(SimpleStringResponse response, boolean value) throws Exception {
            assertThat(response.getStatus(), is("ok"));

            final User databaseUser = itUtil.findUser(user.getId());
            assertThat(databaseUser.getRoles().stream()
                .anyMatch(userRole -> userRole.getRole().getType() == RoleType.ACCEPTOR_USER), is(value));

            final List<StockAcceptorUserHistory> acceptorUserHistoryList = itUtil.getAllStockAcceptorUserHistory(
                stock.getCode(), user.getId()
            );
            assertThat(acceptorUserHistoryList.size(), is(2));
            for (final StockAcceptorUserHistory acceptorUserHistory : acceptorUserHistoryList) {
                assertThat(acceptorUserHistory.getUserId(), is(user.getId()));
                assertThat(acceptorUserHistory.getName(), is(user.getName()));
                assertThat(acceptorUserHistory.getBirthDate(), is(user.getBirthDate()));
                assertThat(acceptorUserHistory.getHashedPhoneNumber(), is(user.getHashedPhoneNumber()));
            }
        }

        @Nested
        class AndNormal {

            @DisplayName("Should return 200 when success")
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk());

                final SimpleStringResponse simpleStringResponse = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    SimpleStringResponse.class
                );

                assertResponse(simpleStringResponse, false);
            }
        }

        @Nested
        class AndNormalHasRoleTwoStocks {
            private Stock secondStock;

            @BeforeEach
            void setUp() {
                secondStock = itUtil.createStock();
                itUtil.createStockAcceptorUser(secondStock.getCode(), user);
                request = new DeleteStockAcceptorUserRequest().userId(user.getId());
            }

            @DisplayName("Should return 200 when success")
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk());

                final SimpleStringResponse simpleStringResponse = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    SimpleStringResponse.class
                );

                assertResponse(simpleStringResponse, true);

                final StockAcceptorUser deletedAcceptor = itUtil.findStockAcceptorUser(stock.getCode()).orElse(null);
                final StockAcceptorUser unDeletedAcceptor = itUtil.findStockAcceptorUser(secondStock.getCode()).orElse(null);
                assertThat(deletedAcceptor, nullValue());
                assertThat(unDeletedAcceptor, notNullValue());
            }
        }

        @Nested
        class AndClosingDigitalDocument {

            @BeforeEach
            void setUp() {
                final Board board = itUtil.createBoard(stock);
                final Post post = itUtil.createPost(board, adminUser.getId(), Boolean.FALSE);
                final LocalDateTime now = LocalDateTime.now();
                final LocalDateTime targetStartDate = now.minusDays(5);
                final LocalDateTime targetEndDate = now.minusDays(1);
                final LocalDate referenceDate = now.toLocalDate();
                itUtil.createDigitalDocument(
                    post, stock, user, DigitalDocumentType.DIGITAL_PROXY, targetStartDate, targetEndDate, referenceDate
                );
            }

            @DisplayName("Should return 200 when success")
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk());

                final SimpleStringResponse simpleStringResponse = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    SimpleStringResponse.class
                );

                assertResponse(simpleStringResponse, false);
            }
        }
    }

    @Nested
    class WhenError {

        @BeforeEach
        void setUp() {
            user = itUtil.createUser();
            request = new DeleteStockAcceptorUserRequest().userId(user.getId());
        }

        @Nested
        class AndHasRoleAndNotFoundStockAcceptorUser {

            @BeforeEach
            void setUp() {
                itUtil.createUserRole(user, RoleType.ACCEPTOR_USER);
            }

            @DisplayName("Should return 400 when has role and not found stock acceptor user")
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "종목에 해당하는 수임인 정보가 없습니다.");
            }
        }

        @Nested
        class AndNotHasAcceptorUserRole {

            @BeforeEach
            void setUp() {
                itUtil.createStockAcceptorUser(stock.getCode(), user);
            }

            @DisplayName("Should return 400 when not has acceptor user role")
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "해당 종목에 수임인으로 선정되지 않은 사용자입니다.");
            }
        }

        @Nested
        class AndHasProcessingDigitalProxy {

            @BeforeEach
            void setUp() {
                itUtil.createUserRole(user, RoleType.ACCEPTOR_USER);
                itUtil.createStockAcceptorUser(stock.getCode(), user);

                final Board board = itUtil.createBoard(stock);
                final Post post = itUtil.createPost(board, adminUser.getId(), Boolean.FALSE);
                final LocalDateTime now = LocalDateTime.now();
                final LocalDateTime targetStartDate = now.minusDays(5);
                final LocalDateTime targetEndDate = now.plusDays(1);
                final LocalDate referenceDate = now.toLocalDate();
                itUtil.createDigitalDocument(
                    post, stock, user, DigitalDocumentType.DIGITAL_PROXY, targetStartDate, targetEndDate, referenceDate
                );
            }

            @DisplayName("Should return 400 when has processing digital proxy document")
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "해당 종목에 수임인으로 진행중인 전자문서가 존재합니다.");
            }
        }

        @Nested
        class AndHasProcessingJointOwnership {

            @BeforeEach
            void setUp() {
                itUtil.createUserRole(user, RoleType.ACCEPTOR_USER);
                itUtil.createStockAcceptorUser(stock.getCode(), user);

                final Board board = itUtil.createBoard(stock);
                final Post post = itUtil.createPost(board, adminUser.getId(), Boolean.FALSE);
                final LocalDateTime now = LocalDateTime.now();
                final LocalDateTime targetStartDate = now.minusDays(5);
                final LocalDateTime targetEndDate = now.plusDays(1);
                final LocalDate referenceDate = now.toLocalDate();
                itUtil.createDigitalDocument(
                    post, stock, user, DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT, targetStartDate, targetEndDate, referenceDate
                );
            }

            @DisplayName("Should return 400 when has processing joint ownership document")
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "해당 종목에 수임인으로 진행중인 전자문서가 존재합니다.");
            }
        }
    }
}
