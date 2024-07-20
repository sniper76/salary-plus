package ag.act.service.digitaldocument;

import ag.act.enums.DigitalDocumentType;
import ag.act.exception.NotFoundException;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentHtmlGenerator;
import ag.act.module.digitaldocumentgenerator.converter.DigitalProxyCertificationFillConverter;
import ag.act.module.digitaldocumentgenerator.converter.JointOwnershipCertificationFillConverter;
import ag.act.module.digitaldocumentgenerator.converter.OtherDocumentCertificationFillConverter;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.PDFRenderService;
import ag.act.module.digitaldocumentgenerator.validator.DigitalProxyCertificationFillValidator;
import ag.act.module.digitaldocumentgenerator.validator.JointOwnershipCertificationFillValidator;
import ag.act.module.digitaldocumentgenerator.validator.OtherDocumentCertificationFillValidator;
import ag.act.service.digitaldocument.certification.DigitalDocumentCertificationGenerator;
import ag.act.service.digitaldocument.certification.DigitalDocumentCertificationGeneratorResolver;
import ag.act.service.digitaldocument.certification.DigitalProxyCertificationGenerator;
import ag.act.service.digitaldocument.certification.JointOwnershipCertificationGenerator;
import ag.act.service.digitaldocument.certification.OtherDocumentCertificationGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static ag.act.TestUtil.shuffleAndGet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@MockitoSettings(strictness = Strictness.LENIENT)
public class DigitalDocumentCertificationGeneratorResolverTest {
    private DigitalDocumentCertificationGeneratorResolver resolver;
    private DigitalProxyCertificationGenerator digitalProxyCertificationGenerator;
    private JointOwnershipCertificationGenerator jointOwnershipCertificationGenerator;
    private OtherDocumentCertificationGenerator otherDocumentCertificationGenerator;
    @Mock
    private PDFRenderService pdfRenderService;
    @Mock
    private DigitalDocumentHtmlGenerator digitalDocumentHtmlGenerator;
    @Mock
    private DigitalProxyCertificationFillConverter digitalProxyCertificationFillConverter;
    @Mock
    private DigitalProxyCertificationFillValidator digitalProxyCertificationFillValidator;
    @Mock
    private JointOwnershipCertificationFillConverter jointOwnershipCertificationFillConverter;
    @Mock
    private JointOwnershipCertificationFillValidator jointOwnershipCertificationFillValidator;
    @Mock
    private OtherDocumentCertificationFillConverter otherDocumentCertificationFillConverter;
    @Mock
    private OtherDocumentCertificationFillValidator otherDocumentCertificationFillValidator;


    @BeforeEach
    void setUp() {
        digitalProxyCertificationGenerator = new DigitalProxyCertificationGenerator(
            pdfRenderService,
            digitalDocumentHtmlGenerator,
            digitalProxyCertificationFillConverter,
            digitalProxyCertificationFillValidator
        );
        jointOwnershipCertificationGenerator = new JointOwnershipCertificationGenerator(
            pdfRenderService,
            digitalDocumentHtmlGenerator,
            jointOwnershipCertificationFillConverter,
            jointOwnershipCertificationFillValidator
        );
        otherDocumentCertificationGenerator = new OtherDocumentCertificationGenerator(
            pdfRenderService,
            digitalDocumentHtmlGenerator,
            otherDocumentCertificationFillConverter,
            otherDocumentCertificationFillValidator
        );

        resolver = new DigitalDocumentCertificationGeneratorResolver(
            shuffleAndGet(
                List.of(
                    digitalProxyCertificationGenerator,
                    jointOwnershipCertificationGenerator,
                    otherDocumentCertificationGenerator
                )
            )
        );
    }

    @Test
    void resolveDigitalProxy() {
        DigitalDocumentCertificationGenerator resolved = resolver.resolve(DigitalDocumentType.DIGITAL_PROXY)
            .orElseThrow(() -> new NotFoundException("could not resolve"));

        assertThat(resolved, is(digitalProxyCertificationGenerator));
    }

    @Test
    void resolveJointOwnership() {
        DigitalDocumentCertificationGenerator resolved = resolver.resolve(DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT)
            .orElseThrow(() -> new NotFoundException("could not resolve"));

        assertThat(resolved, is(jointOwnershipCertificationGenerator));
    }

    @Test
    void resolveOtherDocument() {
        DigitalDocumentCertificationGenerator resolved = resolver.resolve(DigitalDocumentType.ETC_DOCUMENT)
            .orElseThrow(() -> new NotFoundException("could not resolve"));

        assertThat(resolved, is(otherDocumentCertificationGenerator));
    }
}
