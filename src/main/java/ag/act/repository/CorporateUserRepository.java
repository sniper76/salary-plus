package ag.act.repository;

import ag.act.entity.CorporateUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorporateUserRepository extends JpaRepository<CorporateUser, Long> {
    Optional<CorporateUser> findByCorporateNo(String corporateNo);

    Page<CorporateUser> findAllByCorporateNameContaining(String searchKeyword, PageRequest pageRequest);

    Optional<CorporateUser> findByUserIdAndStatus(Long userId, ag.act.model.Status status);
}
