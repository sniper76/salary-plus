package ag.act.service.digitaldocument;

import ag.act.converter.digitaldocument.DigitalDocumentPreviewFillConverter;
import ag.act.converter.digitaldocument.DigitalProxyPreviewFillConverter;
import ag.act.converter.digitaldocument.JointOwnershipDocumentPreviewFillConverter;
import ag.act.converter.digitaldocument.OtherDocumentPreviewFillConverter;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.DigitalProxyFill;
import ag.act.module.digitaldocumentgenerator.model.JointOwnershipDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.OtherDocumentFill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class DigitalDocumentPreviewFillServiceTest {
    @InjectMocks
    private DigitalDocumentPreviewFillService service;
    @Mock
    private List<DigitalDocumentPreviewFillConverter<ag.act.model.PreviewDigitalDocumentRequest, DigitalDocumentFill>>
        digitalDocumentPreviewFillConverters;
    @Mock
    private OtherDocumentPreviewFillConverter otherDocumentPreviewFillConverter;
    @Mock
    private DigitalProxyPreviewFillConverter digitalProxyPreviewFillConverter;
    @Mock
    private JointOwnershipDocumentPreviewFillConverter jointOwnershipDocumentPreviewFillConverter;
    @Mock
    private PreviewDigitalDocumentRequest request;
    @Mock
    private OtherDocumentFill otherDocumentFill;
    @Mock
    private DigitalProxyFill digitalProxyFill;
    @Mock
    private JointOwnershipDocumentFill jointOwnershipDocumentFill;

    @BeforeEach
    void setUp() {
        given(request.getCompanyName()).willReturn(someString(10));
        given(digitalDocumentPreviewFillConverters.stream())
            .willReturn(
                Stream.of(otherDocumentPreviewFillConverter, digitalProxyPreviewFillConverter, jointOwnershipDocumentPreviewFillConverter));
    }

    @Nested
    class WhenTypeIsOtherDocument {
        @Test
        void shouldFillOtherDocumentPreview() {

            // Given
            given(request.getType()).willReturn(DigitalDocumentType.ETC_DOCUMENT.name());
            given(otherDocumentPreviewFillConverter.canConvert(DigitalDocumentType.ETC_DOCUMENT)).willReturn(true);
            given(otherDocumentPreviewFillConverter.apply(request)).willReturn(otherDocumentFill);

            // When
            service.fill(request);

            // Then
            then(otherDocumentPreviewFillConverter).should().apply(request);
        }
    }

    @Nested
    class WhenTypeIsJointOwnershipDocument {
        @Test
        void shouldFillJointOwnershipDocumentPreview() {

            // Given
            given(request.getType()).willReturn(DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT.name());
            given(jointOwnershipDocumentPreviewFillConverter.canConvert(DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT)).willReturn(true);
            given(jointOwnershipDocumentPreviewFillConverter.apply(request)).willReturn(jointOwnershipDocumentFill);

            // When
            service.fill(request);

            // Then
            then(jointOwnershipDocumentPreviewFillConverter).should().apply(request);
        }
    }

    @Nested
    class WhenTypeIsDigitalProxy {
        @Test
        void shouldFillDigitalProxyPreview() {

            // Given
            given(request.getType()).willReturn(DigitalDocumentType.DIGITAL_PROXY.name());
            given(digitalProxyPreviewFillConverter.canConvert(DigitalDocumentType.DIGITAL_PROXY)).willReturn(true);
            given(digitalProxyPreviewFillConverter.apply(request)).willReturn(digitalProxyFill);

            // When
            service.fill(request);

            // Then
            then(digitalProxyPreviewFillConverter).should().apply(request);
        }
    }
}