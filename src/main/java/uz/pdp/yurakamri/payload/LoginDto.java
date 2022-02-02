package uz.pdp.yurakamri.payload;

import lombok.Data;

@Data
public class LoginDto {
    private String fullName;
    private String password;
}
