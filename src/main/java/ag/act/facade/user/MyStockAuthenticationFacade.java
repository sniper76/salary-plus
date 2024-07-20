package ag.act.facade.user;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.user.MyStockAuthenticationResponseConverter;
import ag.act.entity.User;
import ag.act.model.MyStockAuthenticationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MyStockAuthenticationFacade {
    private final MyStockAuthenticationResponseConverter myStockAuthenticationResponseConverter;

    public MyStockAuthenticationResponse getMyStockAuthentication(String stockCode) {
        final User user = ActUserProvider.getNoneNull();
        return myStockAuthenticationResponseConverter.convert(user.getId(), stockCode);
    }
}
