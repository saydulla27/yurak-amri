package uz.pdp.yurakamri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.yurakamri.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
}
