package ag.act.repository;

import ag.act.entity.WebVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WebVerificationRepository extends JpaRepository<WebVerification, Long> {

    Optional<WebVerification> findFirstByVerificationCodeAndAuthenticationReference(
        String verificationCode,
        UUID authenticationReference
    );

    Optional<WebVerification> findFirstByVerificationCodeAndVerificationCodeEndDateTimeGreaterThanEqual(
        String verificationCode,
        LocalDateTime verificationCodeEndDateTime
    );

    List<WebVerification> findAllByAuthenticationReferenceAndUserIdIsNull(UUID authenticationReference);
}
