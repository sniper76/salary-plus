package ag.act.sp.repository;

import ag.act.sp.entity.ShopUserMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopUserMappingRepository extends JpaRepository<ShopUserMapping, Long> {
}
