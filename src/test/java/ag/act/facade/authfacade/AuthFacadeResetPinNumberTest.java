package ag.act.facade.authfacade;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.User;
import ag.act.facade.auth.AuthFacade;
import ag.act.model.SimpleStringResponse;
import ag.act.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;

@MockitoSettings(strictness = Strictness.LENIENT)
class AuthFacadeResetPinNumberTest {

    @InjectMocks
    private AuthFacade facade;
    private List<MockedStatic<?>> statics;
    @Mock
    private UserService userService;
    @Mock
    private User savedUser;
    @Mock
    private User user;
    private SimpleStringResponse result;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));

        // Given
        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(userService.resetPinNumber(user)).willReturn(savedUser);

        // When
        result = facade.resetPinNumber();
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Test
    void shouldResetPinNumber() {
        then(userService).should().resetPinNumber(user);
    }

    @Test
    void shouldInvalidatePinNumberVerificationTime() {
        assertThat(result.getStatus(), is("ok"));
    }
}
