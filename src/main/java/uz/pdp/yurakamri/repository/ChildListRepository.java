package uz.pdp.yurakamri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.yurakamri.entity.ChildList;
import uz.pdp.yurakamri.entity.City;
import uz.pdp.yurakamri.entity.Ketmon;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildListRepository extends JpaRepository<ChildList, Integer> {
    Optional<ChildList> findByBuffer(Long chatid);

    List<ChildList> findAllByUsers(Ketmon user);


}
