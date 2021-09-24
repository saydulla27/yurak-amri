package uz.pdp.yurakamri.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.yurakamri.entity.Region;
import uz.pdp.yurakamri.repository.RegionRepository;

import java.util.List;

@RestController
@RequestMapping("/region")
public class RegionController {
    @Autowired
    RegionRepository regionRepository;

    @GetMapping("/list")
    public List<Region> getList() {
        return regionRepository.findAll();
    }
}
