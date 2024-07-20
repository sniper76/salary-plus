package ag.act.api.admin.stockreferencedate;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.User;
import ag.act.enums.DigitalDocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static ag.act.TestUtil.someLocalDateTime;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

class DeleteStockReferenceDateApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/reference-dates/{referenceDateId}";

    private String jwt;
    private String stockCode;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        stockCode = someStockCode();
    }

    @Nested
    class WhenDeleteStockReferenceDate {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @RepeatedTest(5)
        void shouldReturnSuccess() throws Exception {
            callApi(stubValidStockReferenceDate(stockCode).getId());
        }

        private void callApi(Long stockReferenceDateId) throws Exception {
            MvcResult response = mockMvc
                .perform(
                    delete(TARGET_API, stockCode, stockReferenceDateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.SimpleStringResponse.class
            );

            assertThat(result.getStatus(), is("ok"));
            assertDeletedStockReferenceDateInDatabase(stockReferenceDateId);
        }

        private void assertDeletedStockReferenceDateInDatabase(Long stockReferenceDateId) {
            itUtil.findStockReferenceDate(stockReferenceDateId).map(stockReferenceDate -> {
                throw new RuntimeException("StockReferenceDate should be deleted");
            });
        }

        private StockReferenceDate stubValidStockReferenceDate(String stockCode) {
            return itUtil.createStockReferenceDate(
                stockCode, someLocalDateTime().toLocalDate()
            );
        }
    }

    @Nested
    class WhenFailToDeleteStockReferenceDate {

        private void callApiFailWithNotFoundException(Long stockReferenceDateId) throws Exception {
            MvcResult response = mockMvc
                .perform(
                    delete(TARGET_API, stockCode, stockReferenceDateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().is(404))
                .andReturn();

            itUtil.assertErrorResponse(response, 404, "존재하지 않는 기준일입니다.");
        }

        private void assertStockReferenceDateExistingInDatabase(Long stockReferenceDateId) {
            if (itUtil.findStockReferenceDate(stockReferenceDateId).isEmpty()) {
                throw new RuntimeException("StockReferenceDate should be existing in database");
            }
        }

        @Nested
        class AndNotFoundStockReferenceDate {

            @Test
            void shouldReturnBadRequestException() throws Exception {
                final StockReferenceDate stockReferenceDate = itUtil.createStockReferenceDate(
                    stockCode, someLocalDateTime().toLocalDate()
                );

                callApiFailWithNotFoundException(someLong());
                assertStockReferenceDateExistingInDatabase(stockReferenceDate.getId());
            }
        }

    }

    @Nested
    class AlreadyUsedInDocument {
        private User acceptUser;
        private User postWriteUser;
        private Board board;
        private Post post;
        private Stock stock;
        private StockReferenceDate stockReferenceDate;

        private void callApiFailWithBadRequestException() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    delete(TARGET_API, stock.getCode(), stockReferenceDate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().is(400))
                .andReturn();

            itUtil.assertErrorResponse(response, 400, String.format("전자문서에서 사용된 종목(%s)의 기준일(%s)은 삭제할 수 없습니다.",
                stockReferenceDate.getStockCode(), stockReferenceDate.getReferenceDate()));
        }

        @Test
        void shouldReturnBadRequestException() throws Exception {
            stock = itUtil.createStock();
            acceptUser = itUtil.createUser();
            postWriteUser = itUtil.createUser();
            board = itUtil.createBoard(stock);
            post = itUtil.createPost(board, postWriteUser.getId());
            LocalDateTime targetStartDate = someLocalDateTime();
            LocalDateTime targetEndDate = targetStartDate.plusDays(7);

            stockReferenceDate = itUtil.createStockReferenceDate(
                stock.getCode(), someLocalDateTime().toLocalDate()
            );

            itUtil.createDigitalDocument(
                post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY,
                targetStartDate, targetEndDate, stockReferenceDate.getReferenceDate()
            );

            callApiFailWithBadRequestException();
        }
    }
}
