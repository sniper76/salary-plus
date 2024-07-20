package ag.act.service;

import ag.act.entity.DigitalProxy;
import ag.act.entity.DigitalProxyApproval;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.module.modusign.ModuSignDocument;
import ag.act.service.digitaldocument.modusign.DigitalProxyApprovalService;
import ag.act.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalProxyApprovalServiceTest {

    @InjectMocks
    private DigitalProxyApprovalService service;
    @Mock
    private PostService postService;

    @Nested
    class FindMyDigitalProxyApproval {

        @Mock
        private DigitalProxyApproval digitalProxyApproval;
        private Long userId;

        @BeforeEach
        void setUp() {
            userId = someLong();
            given(digitalProxyApproval.getUserId()).willReturn(userId);
        }

        @Nested
        class WhenListHasMyDigitalProxyApproval {

            @Test
            void shouldFindMyDigitalProxyApproval() {


                // When
                final Optional<DigitalProxyApproval> actual = service.findMyDigitalProxyApproval(List.of(digitalProxyApproval), userId);

                // Then
                assertThat(actual.isPresent(), is(true));
                assertThat(actual.get(), is(digitalProxyApproval));
            }
        }

        @Nested
        class WhenListDoesNotHaveMyDigitalProxyApproval {

            @Test
            void shouldNotFindMyDigitalProxyApproval() {

                // When
                final Optional<DigitalProxyApproval> actual = service.findMyDigitalProxyApproval(List.of(digitalProxyApproval), someLong());

                // Then
                assertThat(actual.isPresent(), is(false));
            }
        }
    }

    @Nested
    class AddDigitalProxyApproval {

        private DigitalProxy digitalProxyStub;
        @Mock
        private ModuSignDocument document;
        @Mock
        private Post post;
        @Mock
        private User user;
        private Long userId;
        private String documentId;
        private String participantId;

        @BeforeEach
        void setUp() {
            userId = someLong();
            documentId = someString(10);
            participantId = someString(20);

            digitalProxyStub = new DigitalProxy();

            given(user.getId()).willReturn(userId);
            given(postService.savePost(post)).willReturn(post);
            given(document.getId()).willReturn(documentId);
            given(document.getParticipantId()).willReturn(participantId);
        }

        @Nested
        class WhenDigitalProxyApprovalIsEmpty {

            @Test
            void shouldAddDigitalProxyApproval() {

                // When
                service.addDigitalProxyApproval(post, digitalProxyStub, user, document);

                // Then
                final List<DigitalProxyApproval> digitalProxyApprovalList = digitalProxyStub.getDigitalProxyApprovalList();
                assertThat(digitalProxyApprovalList.size(), is(1));
                assertThat(digitalProxyApprovalList.get(0).getUserId(), is(userId));
                assertThat(digitalProxyApprovalList.get(0).getDocumentId(), is(documentId));
                assertThat(digitalProxyApprovalList.get(0).getParticipantId(), is(participantId));
                assertThat(digitalProxyApprovalList.get(0).getStatus(), is(ag.act.model.Status.ACTIVE));
                then(postService).should().savePost(post);
            }
        }

        @Nested
        class WhenDigitalProxyApprovalIsNotEmpty {

            @BeforeEach
            void setUp() {
                final ArrayList<DigitalProxyApproval> digitalProxyApprovalList = new ArrayList<>();
                digitalProxyApprovalList.add(new DigitalProxyApproval());
                digitalProxyApprovalList.add(new DigitalProxyApproval());
                digitalProxyStub.setDigitalProxyApprovalList(digitalProxyApprovalList);
            }

            @Test
            void shouldAddDigitalProxyApproval() {

                // When
                service.addDigitalProxyApproval(post, digitalProxyStub, user, document);

                // Then
                final List<DigitalProxyApproval> digitalProxyApprovalList = digitalProxyStub.getDigitalProxyApprovalList();
                assertThat(digitalProxyApprovalList.size(), is(3));
                assertThat(digitalProxyApprovalList.get(2).getUserId(), is(userId));
                assertThat(digitalProxyApprovalList.get(2).getDocumentId(), is(documentId));
                assertThat(digitalProxyApprovalList.get(2).getParticipantId(), is(participantId));
                assertThat(digitalProxyApprovalList.get(2).getStatus(), is(ag.act.model.Status.ACTIVE));
                then(postService).should().savePost(post);
            }
        }
    }
}
