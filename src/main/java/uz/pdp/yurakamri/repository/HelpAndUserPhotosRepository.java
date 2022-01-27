package uz.pdp.yurakamri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.yurakamri.entity.ChildList;
import uz.pdp.yurakamri.entity.HelpAndUsers;
import uz.pdp.yurakamri.entity.HelpAndUsersPhotos;
import uz.pdp.yurakamri.entity.Ketmon;

import java.util.List;
import java.util.Optional;

@Repository
public interface HelpAndUserPhotosRepository extends JpaRepository<HelpAndUsersPhotos, Integer> {
    List<HelpAndUsersPhotos> findAllByHelps( HelpAndUsers helpAndUsers);
}
