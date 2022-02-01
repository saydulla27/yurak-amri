package uz.pdp.yurakamri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.yurakamri.entity.City;
import uz.pdp.yurakamri.entity.Region;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

    List<City> findByRegion_Name (String region);
    Optional<City> findByName (String city);


}
