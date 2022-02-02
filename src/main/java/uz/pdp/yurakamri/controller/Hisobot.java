//package uz.pdp.yurakamri.controller;
//
//import lombok.AllArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import uz.pdp.yurakamri.entity.HelpAndUsers;
//import uz.pdp.yurakamri.entity.Ketmon;
//import uz.pdp.yurakamri.payload.LoginDto;
//import uz.pdp.yurakamri.repository.HelpAndUserRepository;
//import uz.pdp.yurakamri.repository.UserRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//
//@RestController
//@RequestMapping("/api/hisobot")
//public class Hisobot {
//
//    @Autowired
//    UserRepository userRepository;
//
//    @GetMapping("/list")
//    public ResponseEntity<?> getList() {
//        List<Ketmon> all = userRepository.findAll();
//        return ResponseEntity.ok(all);
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
//
//        Optional<Ketmon> byFullNameAndPassword = userRepository.findByFullNameAndPassword(loginDto.getFullName(), loginDto.getPassword());
//      if (!byFullNameAndPassword.isPresent()){
//          return ResponseEntity.ok("Bunday Yoq");
//      }
//      return ResponseEntity.ok("Mavjud");
//    }
//}
