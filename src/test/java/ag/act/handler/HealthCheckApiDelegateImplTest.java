package ag.act.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MockitoSettings(strictness = Strictness.LENIENT)
class HealthCheckApiDelegateImplTest {

    @InjectMocks
    private HealthCheckApiDelegateImpl delegate;

    @Test
    @DisplayName("Should return ok status with headers and body")
    void testHello() {
        final ResponseEntity<ag.act.model.SimpleStringResponse> result = delegate.healthCheck();
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertThat(Objects.requireNonNull(result.getBody()).getStatus(), is("ok"));
    }
}
