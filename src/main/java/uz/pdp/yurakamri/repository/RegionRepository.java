package uz.pdp.yurakamri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.yurakamri.entity.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
}
