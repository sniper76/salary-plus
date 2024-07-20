package ag.act.repository;

import ag.act.entity.Report;
import ag.act.enums.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByTypeAndUserId(ReportType reportType, Long userId);

    Optional<Report> findByContentId(Long contentId);
}
