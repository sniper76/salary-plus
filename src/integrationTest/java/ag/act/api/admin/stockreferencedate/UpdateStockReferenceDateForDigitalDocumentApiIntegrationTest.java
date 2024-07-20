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
import java.util.List;
import java.util.Objects;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateStockReferenceDateForDigitalDocumentApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/digital-document/{digitalDocumentId}/reference-dates/{stockReferenceDateId}";

    private User adminUser;
    private User user;
    private Post post;
    private String adminJwt;
    private User acceptUser;
    private Stock stock;
    private String stockCode;
    private Board board;
    private Solidarity solidarity;
    private CreateStockReferenceDateRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();
        adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());
        user = itUtil.createUser();
        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);

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

    private Long makeStockReferenceDate(LocalDate referenceDate) {
        final StockReferenceDate stockReferenceDate = itUtil.createStockReferenceDate(
            stockCode, referenceDate
        );
        return stockReferenceDate.getId();
    }

    private CreateStockReferenceDateRequest getRequest(LocalDate referenceDate) {
        return new CreateStockReferenceDateRequest()
            .referenceDate(referenceDate);
    }

    @Nested
    class WhenSuccess {
        private Long digitalDocumentId;
        private Long stockReferenceDateId;

        @Nested
        class WhenOnlyOneDigitalDocument {

            private DigitalDocument digitalDocument1;
            private LocalDate referenceDate;
            private LocalDate updateReferenceDate;

            @BeforeEach
            void setUp() {
                final LocalDateTime now = LocalDateTime.now();
                referenceDate = LocalDate.of(2023, 10, 3);
                updateReferenceDate = LocalDate.now();

                stockReferenceDateId = makeStockReferenceDate(referenceDate);

                digitalDocument1 = createDigitalDocumentWithUser(
                    now.plus(2, ChronoUnit.DAYS),
                    now.plus(3, ChronoUnit.DAYS),
                    referenceDate
                );
                digitalDocumentId = digitalDocument1.getId();

                request = getRequest(updateReferenceDate);
            }

            @Test
            void shouldReturnResponse() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        patch(TARGET_API, digitalDocumentId, stockReferenceDateId)
                            .content(objectMapperUtil.toRequestBody(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminJwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

                final ag.act.model.StockReferenceDateDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    ag.act.model.StockReferenceDateDataResponse.class
                );

                final DigitalDocument digitalDocumentDatabase1 = itUtil.findDigitalDocument(digitalDocumentId);

                final StockReferenceDate stockReferenceDateDatabase = itUtil.findStockReferenceDate(stockReferenceDateId).orElseThrow();

                assertThat(stockReferenceDateDatabase.getReferenceDate(), is(request.getReferenceDate()));

                assertThat(result.getData().getStockCode(), is(stockCode));
                assertThat(result.getData().getReferenceDate(), is(request.getReferenceDate()));
                assertThat(
                    digitalDocumentDatabase1.getStockReferenceDate(),
                    is(request.getReferenceDate())
                );
            }
        }

        @Nested
        class WhenMultipleDigitalDocument {

            private DigitalDocument digitalDocument1;
            private DigitalDocument digitalDocument2;
            private DigitalDocument digitalDocument3;
            private LocalDate referenceDate;
            private LocalDate updateReferenceDate;

            @BeforeEach
            void setUp() {
                final LocalDateTime now = LocalDateTime.now();
                referenceDate = LocalDate.of(2023, 10, 3);
                updateReferenceDate = LocalDate.now();

                stockReferenceDateId = makeStockReferenceDate(referenceDate);

                digitalDocument1 = createDigitalDocumentWithUser(
                    now.plus(2, ChronoUnit.DAYS),
                    now.plus(3, ChronoUnit.DAYS),
                    referenceDate
                );
                digitalDocument2 = createDigitalDocumentWithUser(
                    now.plus(3, ChronoUnit.DAYS),
                    now.plus(4, ChronoUnit.DAYS),
                    referenceDate
                );
                digitalDocument3 = createDigitalDocumentWithUser(
                    now.plus(4, ChronoUnit.DAYS),
                    now.plus(5, ChronoUnit.DAYS),
                    referenceDate
                );
                digitalDocumentId = digitalDocument1.getId();

                request = getRequest(updateReferenceDate);
            }

            @Test
            void shouldReturnResponse() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        patch(TARGET_API, digitalDocumentId, stockReferenceDateId)
                            .content(objectMapperUtil.toRequestBody(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminJwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

                final ag.act.model.StockReferenceDateDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    ag.act.model.StockReferenceDateDataResponse.class
                );

                final DigitalDocument digitalDocumentDatabase1 = itUtil.findDigitalDocument(digitalDocumentId);

                final List<StockReferenceDate> stockReferenceDateList = itUtil.findAllStockReferenceDate();

                final StockReferenceDate stockReferenceDateBefore = stockReferenceDateList.stream()
                        .filter(it -> Objects.equals(it.getReferenceDate(), referenceDate))
                            .findFirst().orElseThrow();

                final StockReferenceDate stockReferenceDateAfter = stockReferenceDateList.stream()
                        .filter(it -> Objects.equals(it.getReferenceDate(), request.getReferenceDate()))
                            .findFirst().orElseThrow();

                assertThat(stockReferenceDateBefore, is(notNullValue()));
                assertThat(stockReferenceDateAfter, is(notNullValue()));

                assertThat(result.getData().getStockCode(), is(stockCode));
                assertThat(result.getData().getReferenceDate(), is(request.getReferenceDate()));
                assertThat(
                    digitalDocumentDatabase1.getStockReferenceDate(),
                    is(request.getReferenceDate())
                );
            }
        }
    }

    @Nested
    class WhenError {
        private Long digitalDocumentId;
        private Long stockReferenceDateId;

        @Nested
        class WhenAlreadyTargetStartDate {

            private DigitalDocument digitalDocument1;
            private LocalDate referenceDate;
            private LocalDate updateReferenceDate;

            @BeforeEach
            void setUp() {
                final LocalDateTime now = LocalDateTime.now();
                referenceDate = LocalDate.of(2023, 10, 3);
                updateReferenceDate = LocalDate.now();

                stockReferenceDateId = makeStockReferenceDate(referenceDate);

                digitalDocument1 = createDigitalDocumentWithUser(
                    now.minus(3, ChronoUnit.DAYS),
                    now.plus(3, ChronoUnit.DAYS),
                    referenceDate
                );
                digitalDocumentId = digitalDocument1.getId();

                request = getRequest(updateReferenceDate);
            }

            @Test
            void shouldReturnResponse() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        patch(TARGET_API, digitalDocumentId, stockReferenceDateId)
                            .content(objectMapperUtil.toRequestBody(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + adminJwt)
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn();

                itUtil.assertErrorResponse(response, 400, "기준일을 변경하려는 전자문서 참여일이 이미 시작되었습니다.");
            }

        }
    }
}
