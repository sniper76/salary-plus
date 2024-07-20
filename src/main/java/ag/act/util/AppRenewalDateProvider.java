package ag.act.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class AppRenewalDateProvider {
    private final LocalDate appRenewalDate;

    public AppRenewalDateProvider(
        @Value("${act.app-renewal-date:2023-08-25}") String appRenewalDate
    ) {
        this.appRenewalDate = LocalDate.parse(appRenewalDate);
    }

    public LocalDate get() {
        return appRenewalDate;
    }

    public LocalDateTime getStartOfDay() {
        return appRenewalDate.atStartOfDay();
    }
}
