package ag.act.module.digitaldocumentgenerator.converter;


import ag.act.module.digitaldocumentgenerator.dto.AttachingFilesDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class AttachingFilesDescriptionConverterTest {
    @InjectMocks
    private AttachingFilesDescriptionConverter converter;

    @ParameterizedTest(name = "{index} => expectedDescription=''{1}''")
    @MethodSource("attachingFilesProvider")
    void shouldCalculateSuccess(AttachingFilesDto attachingFilesDto, String expectedDescription) {
        String actual = converter.convert(attachingFilesDto);

        assertThat(actual, is(expectedDescription));
    }

    private static Stream<Arguments> attachingFilesProvider() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of(getAttachingFilesDto(List.of()), null),
            Arguments.of(
                getAttachingFilesDto(List.of("idCardImage")),
                "신분증 사본"
            ),
            Arguments.of(
                getAttachingFilesDto(List.of("bankAccountImages")),
                "잔고증명서/주식보유명세서 등 보유주식 증빙자료"
            ),
            Arguments.of(
                getAttachingFilesDto(List.of("idCardImage", "bankAccountImages")),
                "신분증 사본, 잔고증명서/주식보유명세서 등 보유주식 증빙자료"
            ),
            Arguments.of(
                getAttachingFilesDto(List.of("hectoEncryptedBankAccountPdf")),
                "잔고증명서/주식보유명세서 등 보유주식 증빙자료"
            ),
            Arguments.of(
                getAttachingFilesDto(List.of("idCardImage", "hectoEncryptedBankAccountPdf")),
                "신분증 사본, 잔고증명서/주식보유명세서 등 보유주식 증빙자료"
            )
        );
    }

    private static AttachingFilesDto getAttachingFilesDto(List<String> setFields) {
        return AttachingFilesDto.builder()
            .idCardImage(setFields.contains("idCardImage") ? mockImageFile() : null)
            .bankAccountImages(
                setFields.contains("bankAccountImages") ? List.of(mockImageFile()) : null
            )
            .hectoEncryptedBankAccountPdf(
                setFields.contains("hectoEncryptedBankAccountPdf") ? mockPdfFile() : null
            ).build();
    }

    private static MockMultipartFile mockImageFile() {
        return new MockMultipartFile(someString(10), someString(10), "image/jpeg", new byte[0]);
    }

    private static MockMultipartFile mockPdfFile() {
        return new MockMultipartFile(someString(10), someString(10), "application/pdf", new byte[0]);
    }

}