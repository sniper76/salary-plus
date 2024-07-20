package ag.act.repository;

import ag.act.entity.FileContent;
import ag.act.enums.FileContentType;
import ag.act.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileContentRepository extends JpaRepository<FileContent, Long> {

    List<FileContent> findByIdIn(List<Long> imageIdList);

    List<FileContent> findAllByFileContentTypeAndStatusIn(FileContentType fileContentType, List<Status> statuses);

    Optional<FileContent> findByFilename(String filename);
}
