package ag.act.core.aop.htmlcontent;

import ag.act.model.UpdateCommentRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("checkstyle:LineLength")
@MockitoSettings(strictness = Strictness.LENIENT)
class HtmlContentEscaperTest {

    @Nested
    class RequestWithIsEdTrue {

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of("<div>&lt;테스트&gt;</div>", "<div>&lt;테스트&gt;</div>"),
                Arguments.of("<script>test</script><div>&lt;테스트&gt;</div>", "<div>&lt;테스트&gt;</div>"),
                Arguments.of("<div>&lt;테스트&gt;&lt;img src='test.com' /&gt;</div><script>test</script>", "<div>&lt;테스트&gt;&lt;img src='test.com' /&gt;</div>"),
                Arguments.of("<div>&lt;테스트&gt;&lt;img src=\"test.com\" /&gt;</div><script>test</script>", "<div>&lt;테스트&gt;&lt;img src=\"test.com\" /&gt;</div>"),
                Arguments.of("<div>&lt;테스트&gt;<script>test</script>\n\n</div>", "<div>&lt;테스트&gt;\n\n</div>"),
                Arguments.of("<script>test</script>", ""),
                Arguments.of("<SCRIPT>test</script>", ""),
                Arguments.of("<SCRIPT>test</SCRIPT>", ""),
                Arguments.of("""
                    <div>&lt;테스트&gt;<script>
                    alert('test');
                    </script>
                    </div>""", "<div>&lt;테스트&gt;\n</div>")
            );
        }

        @ParameterizedTest(name = "{index} => requestContent=''{0}'', expectedContent=''{1}''")
        @MethodSource("valueProvider")
        void shouldRemoveScript(String requestContent, String expectedContent) {
            // Given
            UpdateCommentRequest request = new UpdateCommentRequest()
                .content(requestContent)
                .isEd(Boolean.TRUE);

            // When
            final String actualContent = new HtmlContentEscaper(request).escapeContent();

            // Then
            assertThat(actualContent, is(expectedContent));
        }
    }

    @Nested
    class RequestWithIsEdFalse {

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of("<div><테스트>111</div>", "&lt;div&gt;&lt;테스트&gt;111&lt;/div&gt;"),
                Arguments.of("<div><테스트>111</div><script>", "&lt;div&gt;&lt;테스트&gt;111&lt;/div&gt;&lt;script&gt;"),
                Arguments.of("<div><테스트>111</div><script></script>", "&lt;div&gt;&lt;테스트&gt;111&lt;/div&gt;&lt;script&gt;&lt;/script&gt;"),
                Arguments.of("<div><테스트>111</div><script  language='javascript'></script>", "&lt;div&gt;&lt;테스트&gt;111&lt;/div&gt;&lt;script&nbsp; language='javascript'&gt;&lt;/script&gt;"),
                Arguments.of("<script  language='javascript'></script>", "&lt;script&nbsp; language='javascript'&gt;&lt;/script&gt;"),
                Arguments.of("<SCRIPT  language='javascript'></script>", "&lt;SCRIPT&nbsp; language='javascript'&gt;&lt;/script&gt;"),
                Arguments.of("<SCRIPT  language='javascript'></ script >", "&lt;SCRIPT&nbsp; language='javascript'&gt;&lt;/ script &gt;"),
                Arguments.of("<p></p>", "&lt;p&gt;&lt;/p&gt;"),
                Arguments.of("<></>", "&lt;&gt;&lt;/&gt;"),
                Arguments.of("<>", "&lt;&gt;"),
                Arguments.of("<TEST>", "&lt;TEST&gt;"),
                Arguments.of("<>TEST<>", "&lt;&gt;TEST&lt;&gt;"),
                Arguments.of("<p>&TEST TEST2<></p>", "&lt;p&gt;&amp;TEST TEST2&lt;&gt;&lt;/p&gt;"),
                Arguments.of("<오픈채팅방> https://naver.com", "&lt;오픈채팅방&gt; https://naver.com"),
                Arguments.of("<p>", "&lt;p&gt;"),
                Arguments.of("&&&&", "&amp;&amp;&amp;&amp;"),
                Arguments.of("안녕하세요.\\n이번 주주운동을 펼칠 <script>alert(\"bomb\")</script> 주주대표 이대표입니다.", "안녕하세요.\\n이번 주주운동을 펼칠 &lt;script&gt;alert(\"bomb\")&lt;/script&gt; 주주대표 이대표입니다."),
                Arguments.of("<img src=\"https://www.naver.com\">", "&lt;img src=\"https://www.naver.com\"&gt;"),
                Arguments.of("test<br>test<br>test<br>test<br>", "test&lt;br&gt;test&lt;br&gt;test&lt;br&gt;test&lt;br&gt;"),
                Arguments.of("test\\ntest\\ntest", "test\\ntest\\ntest"),
                Arguments.of("test\ntest\ntest", "test\ntest\ntest"),
                Arguments.of("<div>test\ntest\ntest</div>", "&lt;div&gt;test\ntest\ntest&lt;/div&gt;"),
                Arguments.of("https://www.test.co.kr/view?code=freeb&No=3108622", "https://www.test.co.kr/view?code=freeb&No=3108622"),
                Arguments.of("https://www.test.co.kr/view?code=freeb&No=3108622&&&", "https://www.test.co.kr/view?code=freeb&No=3108622&&&"),
                Arguments.of("<img src=\"https://www.act.co.kr/view?code=freeb&No=3108622\">", "&lt;img src=\"https://www.act.co.kr/view?code=freeb&No=3108622\"&gt;"),
                Arguments.of("<img src='https://www.act.co.kr/view?code=freeb&No=3108622'>", "&lt;img src='https://www.act.co.kr/view?code=freeb&No=3108622'&gt;")
            );
        }

        @ParameterizedTest(name = "{index} => requestContent=''{0}'', expectedContent=''{1}''")
        @MethodSource("valueProvider")
        void shouldRemoveScript(String requestContent, String expectedContent) {
            // Given
            UpdateCommentRequest request = new UpdateCommentRequest()
                .content(requestContent)
                .isEd(Boolean.FALSE);

            // When
            final String actualContent = new HtmlContentEscaper(request).escapeContent();

            // Then
            assertThat(actualContent, is(expectedContent));
        }
    }
}
