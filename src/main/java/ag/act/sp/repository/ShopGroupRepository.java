package ag.act.sp.repository;

import ag.act.sp.entity.ShopGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopGroupRepository extends JpaRepository<ShopGroup, Long> {
}
