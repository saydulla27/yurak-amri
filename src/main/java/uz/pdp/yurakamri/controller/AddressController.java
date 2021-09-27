package uz.pdp.yurakamri.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.pdp.yurakamri.entity.Address;
import uz.pdp.yurakamri.entity.Region;
import uz.pdp.yurakamri.payload.AddressDto;
import uz.pdp.yurakamri.payload.ApiResponse;
import uz.pdp.yurakamri.repository.AddressRepository;
import uz.pdp.yurakamri.repository.RegionRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    RegionRepository regionRepository;

    @GetMapping("/addressList")
    public List<Address> getList() {
        return addressRepository.findAll();
    }

    @GetMapping("/getOne/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        Optional<Address> byId = addressRepository.findById(id);
        return byId.map(address -> new ApiResponse("Found!", true, address)).orElseGet(() -> new ApiResponse("Not found", false));
    }

    @PostMapping("/add")
    public ApiResponse addAddress(@RequestBody AddressDto addressDto) {
        Address address = new Address();
        address.setHome(addressDto.getHome());
        Optional<Region> optionalRegion = regionRepository.findById(addressDto.getRegionId());
        if (!optionalRegion.isPresent()) return new ApiResponse("Region not found", false);
        address.setRegion(optionalRegion.get());
        addressRepository.save(address);
        return new ApiResponse("Saved!", true);
    }

    @PutMapping("/edit/{id}")
    public ApiResponse edit(@PathVariable Integer id, @RequestBody AddressDto addressDto) {
        Optional<Address> optionalAddress = addressRepository.findById(id);
        if (!optionalAddress.isPresent()) {
            return new ApiResponse(" not found!", false);
        }

        Address a = optionalAddress.get();
        Optional<Region> optionalRegion = regionRepository.findById(addressDto.getRegionId());
        if (!optionalRegion.isPresent()) {
            return new ApiResponse("Region not found!", false);
        }
        a.setRegion(optionalRegion.get());
        a.setHome(addressDto.getHome());
        a.setStreet(addressDto.getStreet());
        addressRepository.save(a);
        return new ApiResponse("Update!", true);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        Optional<Address> optionalAddress = addressRepository.findById(id);
        if (!optionalAddress.isPresent()) {
            return new ApiResponse("Not found!", false);
        }
        addressRepository.deleteById(id);
        return new ApiResponse("Deleted!", true);
    }


}
