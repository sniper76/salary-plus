package ag.act.api.admin.solidarity;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.model.SolidarityDataResponse;
import ag.act.model.SolidarityResponse;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateSolidarityToActiveIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/solidarity/{solidarityId}/active";

    private String jwt;
    private Stock stock;
    private Solidarity solidarity;


    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        solidarity = itUtil.createSolidarity(stock.getCode());
    }

    private MvcResult callApi(int expectedStatus) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, solidarity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().is(expectedStatus))
            .andReturn();
    }

    @Nested
    class WhenUpdateSolidarityToActive {

        @BeforeEach
        void setUp() {
            solidarity.setStatus(ag.act.model.Status.INACTIVE_BY_ADMIN);
            itUtil.updateSolidarity(solidarity);
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            SolidarityDataResponse result = getDataResponse(callApi(HttpStatus.OK.value()));
            assertResponse(result);
        }

        private void assertResponse(ag.act.model.SolidarityDataResponse result) {
            final SolidarityResponse solidarityResponse = result.getData();

            assertThat(solidarityResponse.getStatus(), is(Status.ACTIVE));
            assertThat(solidarityResponse.getCode(), is(stock.getCode()));
            assertThat(solidarityResponse.getName(), is(stock.getName()));
            assertThat(solidarityResponse.getStake(), is(0f));
            assertThat(solidarityResponse.getMemberCount(), is(0));
            assertThat(solidarityResponse.getRequiredMemberCount(), is(50));
        }

        private ag.act.model.SolidarityDataResponse getDataResponse(MvcResult response) throws Exception {
            return objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.SolidarityDataResponse.class
            );
        }
    }

    @Nested
    class FailToUpdateSolidarityToActive {

        @Nested
        class WhenStatusIsAlreadyActive {
            @BeforeEach
            void setUp() {

                // Given
                solidarity.setStatus(ag.act.model.Status.ACTIVE);
                itUtil.updateSolidarity(solidarity);
            }

            @Test
            void shouldReturnBadRequestErrorResponse() throws Exception {

                // When
                MvcResult response = callApi(HttpStatus.BAD_REQUEST.value());

                // Then
                itUtil.assertErrorResponse(response, HttpStatus.BAD_REQUEST.value(), "이미 활성화된 연대입니다.");
            }
        }

        @Nested
        class WhenStatusIsInvalid {
            @ParameterizedTest
            @MethodSource("invalidStatusProvider")
            void shouldReturnInternalServerErrorResponse(Status status) throws Exception {

                // Given
                solidarity.setStatus(status);
                itUtil.updateSolidarity(solidarity);

                // When
                MvcResult response = callApi(HttpStatus.INTERNAL_SERVER_ERROR.value());

                // Then
                itUtil.assertErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR.value(), "연대 상태가 활성화 대기 상태가 아닙니다.");
            }

            private static Stream<Arguments> invalidStatusProvider() {
                return Stream.of(
                    Arguments.of(Status.DELETED_BY_USER),
                    Arguments.of(Status.DELETED_BY_ADMIN),
                    Arguments.of(Status.INACTIVE_BY_USER),
                    Arguments.of(Status.PROCESSING),
                    Arguments.of(Status.WITHDRAWAL_REQUESTED)
                );
            }

        }

    }
}
