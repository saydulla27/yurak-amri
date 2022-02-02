package uz.pdp.yurakamri.payload;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uz.pdp.yurakamri.entity.City;
import uz.pdp.yurakamri.entity.Ketmon;
import uz.pdp.yurakamri.entity.Region;
import uz.pdp.yurakamri.entity.RequestUsers;
import uz.pdp.yurakamri.entity.enums.Role;
import uz.pdp.yurakamri.repository.CityRepository;
import uz.pdp.yurakamri.repository.RegionRepository;
import uz.pdp.yurakamri.repository.RequestUsersRepository;
import uz.pdp.yurakamri.repository.UserRepository;

@Component
public class Dataloder implements CommandLineRunner {

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RequestUsersRepository requestUsersRepository;
    @Override
    public void run(String... args) throws Exception {

        Ketmon user = new Ketmon();
        user.setPhoneNumber("998917706311");
        user.setRole(Role.ROlE_ADMIN);
        user.setFullName("Sadi");
        user.setPassword("1");
        userRepository.save(user);

        Ketmon user1 = new Ketmon();
        user1.setPhoneNumber("998974340710");
        user1.setRole(Role.ROlE_ADMIN);
        user1.setFullName("Abduraxmon");
        userRepository.save(user1);





    }

}