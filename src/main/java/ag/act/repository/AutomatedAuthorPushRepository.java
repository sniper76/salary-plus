package ag.act.repository;

import ag.act.entity.AutomatedAuthorPush;
import ag.act.enums.AutomatedPushContentType;
import ag.act.enums.AutomatedPushCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutomatedAuthorPushRepository extends JpaRepository<AutomatedAuthorPush, Long> {
    Optional<AutomatedAuthorPush> findByContentIdAndContentTypeAndCriteriaAndCriteriaValue(
        Long contentId, AutomatedPushContentType contentType, AutomatedPushCriteria criteria, Long sumCount
    );

    List<AutomatedAuthorPush> findAllByContentId(Long contentId);

    Optional<AutomatedAuthorPush> findByPushId(Long pushId);
}
