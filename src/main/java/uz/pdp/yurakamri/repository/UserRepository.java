package uz.pdp.yurakamri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.yurakamri.entity.Ketmon;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Ketmon, Integer> {
    Optional<Ketmon> findByChatId(long id);
    Optional<Ketmon> findByPhoneNumber(String phone);

}
