package uz.pdp.yurakamri.payload;

import lombok.Data;

@Data
public class UserDto {
private String rool;
    private long chatId;

    private String state, fullName, phoneNumber;
    private Integer age, childrenCount;
    private Float lat, lon;

    private Integer atachmantId;

}
