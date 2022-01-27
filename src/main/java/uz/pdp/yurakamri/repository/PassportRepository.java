package uz.pdp.yurakamri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.yurakamri.entity.ChildList;
import uz.pdp.yurakamri.entity.Ketmon;
import uz.pdp.yurakamri.entity.Passport;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassportRepository extends JpaRepository<Passport, Integer> {
    List<Passport> findAllByUsers(Ketmon user);


}
