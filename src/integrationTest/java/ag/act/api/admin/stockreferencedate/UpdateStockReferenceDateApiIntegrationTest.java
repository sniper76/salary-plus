package ag.act.api.admin.stockreferencedate;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.CreateStockReferenceDateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateStockReferenceDateApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/reference-dates/{referenceDateId}";

    private User adminUser;
    private User user;
    private Post post;
    private String adminJwt;
    private User acceptUser;
    private Stock stock;
    private String stockCode;
    private Board board;
    private Solidarity solidarity;
    private Long stockReferenceDateId;
    private CreateStockReferenceDateRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();
        adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());
        user = itUtil.createUser();
        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.CO_HOLDING_ARRANGEMENTS);

        solidarity = itUtil.createSolidarity(stockCode);
        UserHoldingStock userHoldingStock = itUtil.createUserHoldingStock(stockCode, adminUser);
        itUtil.updateUserHoldingStock(userHoldingStock);

        acceptUser = itUtil.createUser();
        itUtil.createSolidarityLeader(solidarity, acceptUser.getId());

        post = itUtil.createPost(board, adminUser.getId());
    }

    private DigitalDocument createDigitalDocumentWithUser(
        LocalDateTime targetStartDate, LocalDateTime targetEndDate, LocalDate referenceDate
    ) {
        final DigitalDocument digitalDocument = itUtil.createDigitalDocument(
            post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY,
            targetStartDate, targetEndDate, referenceDate
        );
        post.setDigitalDocument(digitalDocument);
        //post = itUtil.updatePost(post);

        itUtil.createDigitalDocumentUser(digitalDocument, user, stock);
        return digitalDocument;
    }

    private void makeStockReferenceDate(LocalDate referenceDate) {
        final StockReferenceDate stockReferenceDate = itUtil.createStockReferenceDate(
            stockCode, referenceDate
        );
        stockReferenceDateId = stockReferenceDate.getId();
    }

    private CreateStockReferenceDateRequest getRequest(LocalDate referenceDate) {
        return new CreateStockReferenceDateRequest()
            .referenceDate(referenceDate);
    }

    @Nested
    class WhenSuccessUpdateStockReferenceDate {
        private DigitalDocument digitalDocument;

        @BeforeEach
        void setUp() {
            final LocalDateTime now = LocalDateTime.now();
            final LocalDateTime targetStartDate = now.plus(1, ChronoUnit.DAYS);
            final LocalDateTime targetEndDate = now.plus(3, ChronoUnit.DAYS);
            final LocalDate referenceDate = LocalDate.of(2023, 8, 30);
            final LocalDate updateReferenceDate = LocalDate.now();

            makeStockReferenceDate(referenceDate);

            request = getRequest(updateReferenceDate);
        }

        @Test
        void shouldReturnResponse() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API, stockCode, stockReferenceDateId)
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(adminJwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.StockReferenceDateDataResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.StockReferenceDateDataResponse.class
            );

            assertThat(result.getData().getStockCode(), is(stockCode));
            assertThat(result.getData().getReferenceDate(), is(request.getReferenceDate()));
        }
    }

    @Nested
    class WhenUsingDigitalDocumentUpdateStockReferenceDate {
        private DigitalDocument digitalDocument;
        private LocalDate referenceDate;
        private LocalDate updateReferenceDate;

        @BeforeEach
        void setUp() {
            final LocalDateTime now = LocalDateTime.now();
            referenceDate = LocalDate.of(2023, 10, 3);
            updateReferenceDate = LocalDate.now();

            makeStockReferenceDate(referenceDate);

            digitalDocument = createDigitalDocumentWithUser(
                now.plus(2, ChronoUnit.DAYS), now.plus(3, ChronoUnit.DAYS),
                referenceDate
            );
            createDigitalDocumentWithUser(
                now.plus(2, ChronoUnit.DAYS), now.plus(3, ChronoUnit.DAYS),
                referenceDate
            );

            request = getRequest(updateReferenceDate);
        }

        @Test
        void shouldErrorResponse() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API, stockCode, stockReferenceDateId)
                        .content(objectMapperUtil.toRequestBody(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(adminJwt)))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, containsString("기준일에 이미 사용중인 전자문서가 존재합니다. 문서제목"));
        }
    }
}
