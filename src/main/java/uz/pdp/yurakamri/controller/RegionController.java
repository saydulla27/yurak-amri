package uz.pdp.yurakamri.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.pdp.yurakamri.entity.Region;
import uz.pdp.yurakamri.payload.ApiResponse;
import uz.pdp.yurakamri.repository.RegionRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/region")
public class RegionController {
    @Autowired
    RegionRepository regionRepository;

    @GetMapping("/list")
    public List<Region> getList() {
        return regionRepository.findAll();
    }

    @PostMapping("/add")
    public ApiResponse addRegion(@RequestBody Region region) {
        regionRepository.save(region);
        return new ApiResponse("added", true);
    }

    @PutMapping("/edit/{id}")
    public ApiResponse editRegion(@PathVariable Integer id, @RequestBody Region region) {
        Optional<Region> optionalRegion = regionRepository.findById(id);
        if (!optionalRegion.isPresent()) return new ApiResponse("no such id", false);
        Region region1 = optionalRegion.get();
        region1.setName(region.getName());
        regionRepository.save(region1);
        return new ApiResponse("edited", true);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteRegion(@PathVariable Integer id) {
        Optional<Region> optionalRegion = regionRepository.findById(id);
        if (!optionalRegion.isPresent()) return new ApiResponse("no such id", false);
        regionRepository.deleteById(id);
        return new ApiResponse("deleted", true);
    }
}
