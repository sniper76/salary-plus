package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.DigitalProxy;
import ag.act.entity.DigitalProxyApproval;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.facade.digitaldocument.DigitalProxyModuSignFacade;
import ag.act.model.DigitalProxySignResponse;
import ag.act.module.modusign.ModuSignDocument;
import ag.act.module.modusign.ModuSignService;
import ag.act.service.digitaldocument.modusign.DigitalProxyApprovalService;
import ag.act.util.StatusUtil;
import ag.act.validator.document.DigitalProxyModuSignValidator;
import ag.act.validator.post.StockBoardGroupPostValidator;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalProxyModuSignFacadeTest {

    @InjectMocks
    private DigitalProxyModuSignFacade facade;
    private List<MockedStatic<?>> statics;

    @Mock
    private ModuSignService moduSignService;
    @Mock
    private StockBoardGroupPostValidator stockBoardGroupPostValidator;
    @Mock
    private DigitalProxyModuSignValidator digitalProxyModuSignValidator;
    @Mock
    private DigitalProxyApprovalService digitalProxyApprovalService;
    @Mock
    private DigitalProxy digitalProxy;
    @Mock
    private User user;
    @Mock
    private Post post;
    @Captor
    private ArgumentCaptor<ModuSignDocument> moduSignDocumentCaptor;
    private Long userId;
    private Long postId;
    private String stockCode;
    private String boardGroupName;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));

        userId = someLong();
        postId = someLong();
        stockCode = someString(5);
        boardGroupName = someString(5);

        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(user.getId()).willReturn(userId);
        given(post.getId()).willReturn(postId);
        given(stockBoardGroupPostValidator.validateBoardGroupPost(postId, stockCode, boardGroupName, StatusUtil.getDeleteStatuses()))
            .willReturn(post);
        given(post.getDigitalProxy()).willReturn(digitalProxy);
        given(digitalProxyModuSignValidator.validateAndGet(post.getDigitalProxy())).willReturn(digitalProxy);

    }

    @Nested
    class WhenListAlreadyHasMyDigitalProxyApproval {

        @Mock
        private DigitalProxyApproval digitalProxyApproval;
        private String documentId;
        private String participantId;
        private String embeddedUrl;

        @BeforeEach
        void setUp() {
            documentId = someString(15);
            participantId = someString(30);
            embeddedUrl = someString(40);
            final List<DigitalProxyApproval> digitalProxyApprovalList = List.of(digitalProxyApproval);

            given(digitalProxy.getDigitalProxyApprovalList()).willReturn(digitalProxyApprovalList);
            given(digitalProxyApproval.getDocumentId()).willReturn(documentId);
            given(digitalProxyApproval.getParticipantId()).willReturn(participantId);
            given(digitalProxyApprovalService.findMyDigitalProxyApproval(digitalProxyApprovalList, userId))
                .willReturn(Optional.of(digitalProxyApproval));
            given(moduSignService.getEmbeddedUrlInRetry(documentId, participantId)).willReturn(embeddedUrl);

        }

        @Test
        void shouldReturnEmbeddedUrl() {

            // When
            final DigitalProxySignResponse actual = facade.signAndGetEmbeddedUrl(stockCode, boardGroupName, postId);

            // Then
            assertThat(actual.getEmbeddedUrl(), is(embeddedUrl));
            then(digitalProxyModuSignValidator).should().validateModuSignDocument(moduSignDocumentCaptor.capture());
            final ModuSignDocument document = moduSignDocumentCaptor.getValue();
            assertThat(document.getId(), Is.is(documentId));
            assertThat(document.getParticipantId(), Is.is(participantId));
            then(digitalProxyApprovalService).should(never()).addDigitalProxyApproval(post, digitalProxy, user, document);
        }
    }

    @Nested
    class WhenListDoesNotHasMyDigitalProxyApproval {

        private String embeddedUrl;
        @Mock
        private ModuSignDocument document;

        @BeforeEach
        void setUp() {
            final String documentId = someString(15);
            final String participantId = someString(30);
            embeddedUrl = someString(40);

            given(digitalProxy.getDigitalProxyApprovalList()).willReturn(List.of());
            given(digitalProxyApprovalService.findMyDigitalProxyApproval(List.of(), userId))
                .willReturn(Optional.empty());
            given(moduSignService.requestSignature(digitalProxy.getTemplateId(), digitalProxy.getTemplateName(), digitalProxy.getTemplateRole()))
                .willReturn(document);
            given(document.getId()).willReturn(documentId);
            given(document.getParticipantId()).willReturn(participantId);
            given(moduSignService.getEmbeddedUrlInRetry(documentId, participantId)).willReturn(embeddedUrl);

        }

        @Test
        void shouldReturnEmbeddedUrl() {

            // When
            final DigitalProxySignResponse actual = facade.signAndGetEmbeddedUrl(stockCode, boardGroupName, postId);

            // Then
            assertThat(actual.getEmbeddedUrl(), is(embeddedUrl));
            then(digitalProxyModuSignValidator).should().validateModuSignDocument(document);
            then(digitalProxyApprovalService).should().addDigitalProxyApproval(post, digitalProxy, user, document);
        }
    }
}
