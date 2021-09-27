package uz.pdp.yurakamri.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.pdp.yurakamri.entity.Announcement;
import uz.pdp.yurakamri.entity.Ketmon;
import uz.pdp.yurakamri.entity.enums.Status;
import uz.pdp.yurakamri.payload.AnnouncementDto;
import uz.pdp.yurakamri.payload.ApiResponse;
import uz.pdp.yurakamri.repository.AnnouncementRepository;
import uz.pdp.yurakamri.repository.UserRepository;

import java.util.Optional;

@RestController
@RequestMapping("/annoucement")
public class AnnoucementController {

    @Autowired
    AnnouncementRepository announcementRepository;

    @Autowired
    UserRepository userRepository;





    @GetMapping("/get")
    public ApiResponse get(){
        return new ApiResponse("Succeed",true,announcementRepository.findAll());
    }
    @GetMapping("/get/{id}")
    public ApiResponse getById(@PathVariable Integer id){
        Optional<Announcement> announcementOptional = announcementRepository.findById(id);
        if (!announcementOptional.isPresent()) {
            return new ApiResponse("Not Found!",false);
        }
        return new ApiResponse("Succeed",true,announcementOptional.get());
    }
    @PostMapping("/add")
    public ApiResponse add(@RequestBody AnnouncementDto announcementDto){
        Announcement announcement = new Announcement();
        announcement.setDescription(announcementDto.getDescription());
        Optional<Ketmon> optionalKetmon = userRepository.findById(announcementDto.getKetmonId());
        if (!optionalKetmon.isPresent()) {
            return new ApiResponse("Error",false);
        }
        announcement.setUser(optionalKetmon.get());
        if (announcementDto.getStatus().equals(Status.OPEN)) {
            announcement.setStatus(Status.OPEN);
        }
        return new ApiResponse("Saved",true);
    }
}
