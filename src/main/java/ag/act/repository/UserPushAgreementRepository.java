package ag.act.repository;

import ag.act.entity.UserPushAgreement;
import ag.act.enums.push.UserPushAgreementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPushAgreementRepository extends JpaRepository<UserPushAgreement, Long> {
    List<UserPushAgreement> findAllByUserId(Long userId);

    Optional<UserPushAgreement> findByUserIdAndType(Long userId, UserPushAgreementType userPushAgreementType);
}
