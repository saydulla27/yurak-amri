package uz.pdp.yurakamri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.yurakamri.entity.Ketmon;
import uz.pdp.yurakamri.entity.enums.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Ketmon, Integer> {
    Optional<Ketmon> findByChatId(long id);

    Optional<Ketmon> findById(Integer id);

    Optional<Ketmon> findByPhoneNumber(String phone);

    Optional<Ketmon> findByState(String state);

    List<Ketmon> findByRole(Role role);

    Optional<Ketmon> findByBuffer(Long buffer);

    Optional<Ketmon> findByRegion(String region);

    List<Ketmon> findByRoleAndFullNameContainingIgnoreCase(Role role, String name);

    List<Ketmon> findByRoleAndPhoneNumberContainingIgnoreCase(Role role, String tel);

    Optional<Ketmon> findByFullNameAndPassword(String name,String password);





}
