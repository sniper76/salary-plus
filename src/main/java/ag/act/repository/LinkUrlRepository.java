package ag.act.repository;

import ag.act.entity.LinkUrl;
import ag.act.enums.LinkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkUrlRepository extends JpaRepository<LinkUrl, Long> {
    Optional<LinkUrl> findByLinkTypeAndStatus(LinkType linkType, ag.act.model.Status status);
}
