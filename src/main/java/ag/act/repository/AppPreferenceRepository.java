package ag.act.repository;

import ag.act.entity.AppPreference;
import ag.act.enums.AppPreferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppPreferenceRepository extends JpaRepository<AppPreference, Long> {
    Optional<AppPreference> findByType(AppPreferenceType type);
}
