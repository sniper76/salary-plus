package ag.act.service;

import ag.act.entity.FileContent;
import ag.act.enums.FileContentType;
import ag.act.exception.NotFoundException;
import ag.act.model.Gender;
import ag.act.model.Status;
import ag.act.repository.FileContentRepository;
import ag.act.service.io.FileContentService;
import ag.act.validator.DefaultObjectValidator;
import org.apache.commons.lang3.StringUtils;
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

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class FileContentServiceTest {

    @InjectMocks
    private FileContentService service;

    @Mock
    private FileContentRepository fileContentRepository;
    @Mock
    private DefaultObjectValidator defaultObjectValidator;

    @Nested
    class WhenHaveDefaultProfileImages {

        @Mock
        private FileContent fileContent1DescriptionNull;
        @Mock
        private FileContent fileContent2;
        @Mock
        private FileContent fileContent3;
        @Mock
        private FileContent fileContent4DescriptionBlank;
        private List<FileContent> actualFileContents;
        private FileContentType defaultProfile;

        @BeforeEach
        void setUp() {
            defaultProfile = FileContentType.DEFAULT_PROFILE;

            given(fileContent1DescriptionNull.getDescription()).willReturn(null);
            given(fileContent2.getDescription()).willReturn(Gender.M.name());
            given(fileContent3.getDescription()).willReturn(Gender.F.name());
            given(fileContent4DescriptionBlank.getDescription()).willReturn("     ");
            given(fileContentRepository.findAllByFileContentTypeAndStatusIn(defaultProfile, List.of(ag.act.model.Status.ACTIVE)))
                .willReturn(List.of(fileContent1DescriptionNull, fileContent2, fileContent3, fileContent4DescriptionBlank));

        }

        @Nested
        class GetDefaultProfileImages {

            @BeforeEach
            void setUp() {
                actualFileContents = service.getDefaultProfileImages();
            }

            @Test
            void shouldReturnDefaultImages() {
                assertThat(actualFileContents.size(), is(2));
            }

            @Test
            void shouldNotHaveDescriptionEmptyFileContent() {
                assertThat(
                    actualFileContents.stream()
                        .filter(fileContent -> StringUtils.isBlank(fileContent.getDescription()))
                        .count(),
                    is(0L)
                );
            }

            @Test
            void shouldBeMaleFileContentFirst() {
                assertThat(actualFileContents.get(0), is(fileContent2));
            }

            @Test
            void shouldBeFemaleFileContentSecond() {
                assertThat(actualFileContents.get(1), is(fileContent3));
            }
        }

        @Nested
        class GetDefaultProfileImagesByGender {

            private Gender gender;

            @BeforeEach
            void setUp() {
                gender = someEnum(Gender.class);
                actualFileContents = service.getDefaultProfileImagesByGender(gender);
            }

            @Test
            void shouldReturnDefaultImages() {
                assertThat(actualFileContents.size(), is(1));
            }

            @Test
            void shouldBeMatchedDescriptionWithGender() {
                assertThat(actualFileContents.get(0).getDescription(), is(gender.name()));
            }
        }

        @Nested
        class GetPickOneDefaultProfileImage {

            @Mock
            private FileContent fileContent5;
            @Mock
            private FileContent fileContent6;
            private Gender gender;
            private FileContent actualFileContent;

            @BeforeEach
            void setUp() {
                gender = someEnum(Gender.class);
                given(fileContent2.getDescription()).willReturn(someEnum(Gender.class).name());
                given(fileContent3.getDescription()).willReturn(someEnum(Gender.class).name());
                given(fileContent5.getDescription()).willReturn(gender.name());
                given(fileContent6.getDescription()).willReturn(gender.name());
                final List<FileContent> fileContentsFromDatabase = new ArrayList<>() {
                    {
                        add(fileContent1DescriptionNull);
                        add(fileContent2);
                        add(fileContent3);
                        add(fileContent4DescriptionBlank);
                        add(fileContent5);
                        add(fileContent6);
                    }
                };
                given(fileContentRepository.findAllByFileContentTypeAndStatusIn(defaultProfile, List.of(ag.act.model.Status.ACTIVE)))
                    .willReturn(fileContentsFromDatabase);

                willDoNothing().given(defaultObjectValidator).validateNotEmpty(anyList(), eq("기본 프로필 이미지가 존재하지 않습니다."));

                actualFileContent = service.getPickOneDefaultProfileImage(gender);
            }

            @Test
            void shouldBeMatchedDescriptionWithGender() {
                assertThat(actualFileContent.getDescription(), is(gender.name()));
            }
        }
    }

    @Nested
    class DeleteFile {

        @Mock
        private FileContent fileContent;
        @Mock
        private FileContent deletedFileContent;
        private Long fileId;
        private FileContent actualFileContent;

        @BeforeEach
        void setUp() {
            fileId = someLong();

            given(fileContentRepository.findById(fileId)).willReturn(Optional.of(fileContent));
        }

        @Nested
        class WhenDeleteFile {
            @BeforeEach
            void setUp() {
                given(fileContentRepository.save(fileContent)).willReturn(deletedFileContent);

                actualFileContent = service.delete(fileId);
            }

            @Test
            void shouldReturnTheDeletedFileContent() {
                assertThat(actualFileContent, is(deletedFileContent));
            }

            @Test
            void shouldSoftDeleteFileContent() {
                then(fileContent).should().setStatus(Status.DELETED_BY_USER);
            }
        }

        @Nested
        class WhenNotFoundFile {
            @BeforeEach
            void setUp() {
                given(fileContentRepository.findById(fileId)).willReturn(Optional.empty());
            }

            @Test
            void shouldThrowException() {
                assertException(
                    NotFoundException.class,
                    () -> service.delete(fileId),
                    "존재하지 않는 파일입니다."
                );
            }
        }
    }
}
