package uz.pdp.yurakamri.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.pdp.yurakamri.entity.Attachment;
import uz.pdp.yurakamri.entity.Ketmon;
import uz.pdp.yurakamri.entity.enums.Role;
import uz.pdp.yurakamri.payload.ApiResponse;
import uz.pdp.yurakamri.payload.UserDto;
import uz.pdp.yurakamri.repository.AttachmentRepository;
import uz.pdp.yurakamri.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AttachmentRepository attachmentRepository;

    @PostMapping("/add")
    public ApiResponse add(@RequestBody UserDto userDto) {
        Ketmon ketmon = new Ketmon();

        if (userDto.getRool().equals(Role.ROLE_USER)) {
            ketmon.setRole(Role.ROLE_USER);
        }

        ketmon.setFullName(userDto.getFullName());
        ketmon.setAge(userDto.getAge());

        Optional<Attachment> byId = attachmentRepository.findById(userDto.getAtachmantId());
        ketmon.setAttachment(byId.get());

        userRepository.save(ketmon);
        return new ApiResponse("saved",true);
    }
    @PutMapping("/edit/{id}")
    public ApiResponse edit(@PathVariable Integer id,@RequestBody UserDto userDto){
        Optional<Ketmon> optionalKetmon = userRepository.findById(id);
        if (!optionalKetmon.isPresent()) return new ApiResponse("Not found",false);
        Ketmon ketmon = optionalKetmon.get();
        ketmon.setFullName(userDto.getFullName());
        ketmon.setAge(userDto.getAge());

        if (userDto.getAtachmantId()!=null) {

        }
        userRepository.save(ketmon);
        return new ApiResponse("Updated", true);
    }

    @GetMapping("/list")
    public List<Ketmon> list(){
        return userRepository.findAll();
    }

    @DeleteMapping("/delet/{id}")
    public ApiResponse delet(@PathVariable Integer id){
        Optional<Ketmon> byId = userRepository.findById(id);
        if (!byId.isPresent()) return new ApiResponse("Not",false);

        userRepository.deleteById(id);

        return new ApiResponse("deleted", true);
    }
}
