package uz.pdp.yurakamri.payload;

import lombok.Data;

@Data
public class AddressDto {
    private String home, street;
    private Integer regionId;

}
