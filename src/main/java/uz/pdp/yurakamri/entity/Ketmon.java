package uz.pdp.yurakamri.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import uz.pdp.yurakamri.entity.enums.HelpType;
import uz.pdp.yurakamri.entity.enums.Role;
import uz.pdp.yurakamri.entity.enums.Status;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ketmon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Role role;

    private long chatId;
    private long buffer;

    private String state, fullName, age, phoneNumber;
    private Integer childrenCount;
    private Float lat, lon;
    private String password;
    private String date;
    private String status;
    private String company;
    private String region;
    private String street_home;
    private String description;
    private String helpTypeList;
    private String whom;
    private String info_man;


}
