package ag.act.api.digitalproxy;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.DigitalProxyApproval;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.module.modusign.ModuSignDocument;
import ag.act.module.modusign.ModuSignEmbeddedUrlResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someLocalDateTimeInTheFuture;
import static ag.act.TestUtil.someLocalDateTimeInThePast;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class SignAndGetEmbeddedUrlApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}/digital-proxy";

    private String jwt;
    private Stock stock;
    private Board board;
    private Post post;
    private User user;
    private String embeddedUrl;
    private String documentId;
    private String participantId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
        post = itUtil.createPost(board, user.getId());

        final LocalDateTime targetStartDate = someLocalDateTimeInThePast();
        final LocalDateTime targetEndDate = someLocalDateTimeInTheFuture();

        itUtil.createDigitalProxy(post, targetStartDate, targetEndDate);

        documentId = someAlphanumericString(10);
        participantId = someAlphanumericString(10);
        embeddedUrl = someAlphanumericString(10);
        given(moduSignHttpClientUtil.get(anyString())).willReturn(ModuSignEmbeddedUrlResponse.builder().embeddedUrl(embeddedUrl).build());
        given(moduSignHttpClientUtil.post(anyString(), anyMap())).willReturn(
            ModuSignDocument.builder().id(documentId).participantId(participantId).build()
        );
    }

    @Nested
    class WhenSignAndGetEmbeddedUrl {

        @Nested
        class WhenSignAndGetEmbeddedUrlSuccess {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        post(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

                final ag.act.model.DigitalProxySignResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    ag.act.model.DigitalProxySignResponse.class
                );

                assertResponse(result);
                then(moduSignHttpClientUtil).should().get(anyString());
                then(moduSignHttpClientUtil).should().post(anyString(), anyMap());
            }
        }

        @Nested
        class WhenDigitalProxyIsAlreadyFinished {

            @BeforeEach
            void setUp() {

                post = itUtil.createPost(board, user.getId());

                final LocalDateTime targetStartDate = someLocalDateTimeInThePast().minusDays(2);
                final LocalDateTime targetEndDate = targetStartDate.plusDays(1);

                itUtil.createDigitalProxy(post, targetStartDate, targetEndDate);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        post(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn();

                itUtil.assertErrorResponse(response, 400, "이미 위임기간이 종료되었습니다.");
            }
        }

        @Nested
        class WhenDigitalProxyStartedYet {

            @BeforeEach
            void setUp() {

                post = itUtil.createPost(board, user.getId());

                final LocalDateTime targetStartDate = someLocalDateTimeInTheFuture();
                final LocalDateTime targetEndDate = targetStartDate.plusDays(10);

                itUtil.createDigitalProxy(post, targetStartDate, targetEndDate);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        post(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn();

                itUtil.assertErrorResponse(response, 400, "아직 위임 가능 기간이 아닙니다.");
            }
        }

    }

    @Nested
    class WhenDigitalProxyApprovalAlreadyExist {

        @Nested
        class WhenDigitalProxyIsStillGoingOn {
            @BeforeEach
            void setUp() {
                itUtil.createDigitalProxyApproval(post, user.getId(), documentId, participantId);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        post(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

                final ag.act.model.DigitalProxySignResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    ag.act.model.DigitalProxySignResponse.class
                );

                assertResponse(result);
                then(moduSignHttpClientUtil).should().get(anyString());
                then(moduSignHttpClientUtil).should(never()).post(anyString(), anyMap());
            }
        }

        @Nested
        class WhenDigitalProxyIsAlreadyFinished {
            @BeforeEach
            void setUp() {

                post = itUtil.createPost(board, user.getId());

                final LocalDateTime targetStartDate = someLocalDateTimeInThePast().minusDays(2);
                final LocalDateTime targetEndDate = targetStartDate.plusDays(1);

                itUtil.createDigitalProxy(post, targetStartDate, targetEndDate);
                itUtil.createDigitalProxyApproval(post, user.getId(), documentId, participantId);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        post(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

                final ag.act.model.DigitalProxySignResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    ag.act.model.DigitalProxySignResponse.class
                );

                assertResponse(result);
                then(moduSignHttpClientUtil).should().get(anyString());
                then(moduSignHttpClientUtil).should(never()).post(anyString(), anyMap());
            }
        }

    }

    private void assertResponse(ag.act.model.DigitalProxySignResponse result) {
        assertThat(result.getEmbeddedUrl(), is(embeddedUrl));

        final Optional<Post> optionalPost = itUtil.findPostAndDigitalProxy(post.getId());
        assertThat(optionalPost.isPresent(), is(true));
        optionalPost.ifPresent(post -> {
            final List<DigitalProxyApproval> digitalProxyApprovalList = post.getDigitalProxy().getDigitalProxyApprovalList();
            assertThat(digitalProxyApprovalList.size(), is(1));
            assertThat(digitalProxyApprovalList.get(0).getParticipantId(), is(participantId));
            assertThat(digitalProxyApprovalList.get(0).getDocumentId(), is(documentId));
            assertThat(digitalProxyApprovalList.get(0).getUserId(), is(user.getId()));
        });
    }
}
