package ag.act.entity;

import ag.act.module.auth.web.dto.WebVerificationCodeDto;
import ag.act.module.auth.web.dto.WebVerificationDateTimePeriod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "web_verifications")
public class WebVerification implements ActEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "verification_code", nullable = false)
    private String verificationCode;

    @Column(name = "verification_code_base_date_time", nullable = false)
    private LocalDateTime verificationCodeBaseDateTime;

    @Column(name = "verification_code_start_date_time", nullable = false)
    private LocalDateTime verificationCodeStartDateTime;

    @Column(name = "verification_code_end_date_time", nullable = false)
    private LocalDateTime verificationCodeEndDateTime;

    @Column(name = "verification_code_redeemed_at")
    private LocalDateTime verificationCodeRedeemedAt;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "authentication_reference")
    private UUID authenticationReference;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static WebVerification of(
        WebVerificationCodeDto webVerificationCodeDto,
        UUID authenticationReference,
        WebVerificationDateTimePeriod webVerificationDateTimePeriod
    ) {
        WebVerification webVerification = new WebVerification();
        webVerification.setAuthenticationReference(authenticationReference);
        webVerification.setVerificationCode(webVerificationCodeDto.verificationCode());
        webVerification.setVerificationCodeBaseDateTime(webVerificationCodeDto.baseDateTime());
        webVerification.setVerificationCodeStartDateTime(webVerificationDateTimePeriod.verificationCodeStartDateTime());
        webVerification.setVerificationCodeEndDateTime(webVerificationDateTimePeriod.verificationCodeEndDateTime());

        return webVerification;
    }
}
