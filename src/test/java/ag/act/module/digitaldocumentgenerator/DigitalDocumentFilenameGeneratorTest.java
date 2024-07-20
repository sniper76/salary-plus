package ag.act.module.digitaldocumentgenerator;

import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.module.digitaldocumentgenerator.dto.DigitalDocumentFilenameDto;
import ag.act.service.post.PostService;
import ag.act.service.stock.StockService;
import ag.act.service.user.UserService;
import ag.act.util.FilenameUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalDocumentFilenameGeneratorTest {
    @InjectMocks
    private DigitalDocumentFilenameGenerator generator;
    @Mock
    private StockService stockService;
    @Mock
    private PostService postService;
    @Mock
    private UserService userService;
    @Mock
    private Stock stock;
    @Mock
    private Post post;
    @Mock
    private User user;
    private DigitalDocument digitalDocument;
    private Long userId;
    private List<MockedStatic<?>> statics;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(FilenameUtil.class));

        digitalDocument = new DigitalDocument();
        final String stockCode = someStockCode();
        final Long postId = someLong();
        userId = someLong();

        digitalDocument.setStockCode(stockCode);
        digitalDocument.setPostId(postId);

        given(stockService.findByCode(stockCode)).willReturn(Optional.of(stock));
        given(postService.findById(postId)).willReturn(Optional.of(post));
        given(userService.getUser(userId)).willReturn(user);
    }

    @Nested
    class WhenGenerate {
        private String digitalDocumentFileName;

        @BeforeEach
        void setUp() {
            digitalDocumentFileName = someAlphaString();
            given(FilenameUtil.getDigitalDocumentFilename(any(DigitalDocumentFilenameDto.class)))
                .willReturn(digitalDocumentFileName);
        }

        @Test
        void shouldGenerate() {
            // When
            String actual = generator.generate(digitalDocument, userId);

            // Then
            assertThat(actual, is(digitalDocumentFileName));
        }
    }

    @Nested
    class WhenGenerateCertificationName {
        private String certificationName;

        @BeforeEach
        void setUp() {
            certificationName = someAlphaString();
            given(FilenameUtil.getDigitalDocumentCertificationFilename(any(DigitalDocumentFilenameDto.class)))
                .willReturn(certificationName);
        }

        @Test
        void shouldGenerateCertificationName() {
            // When
            String actual = generator.generateCertificationFilename(digitalDocument, userId);

            // Then
            assertThat(actual, is(certificationName));
        }
    }
}
