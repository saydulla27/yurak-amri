package uz.pdp.yurakamri.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.yurakamri.entity.enums.Role;

import javax.persistence.*;
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
    private String childrenInfo;
    private Float lat, lon;
    private String password;
    private boolean status;
    private boolean blacklist;
    private String childAge;
    private String city;
    private String rayon;
    private String street_home;
    private String description;
    private String info_man;
    private String username;


    @OneToMany(mappedBy = "users",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<ChildList> helpLists;


    @OneToMany(mappedBy = "users",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<HelpAndUsers> helpAndUsers;


    @OneToMany(mappedBy = "users",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Passport> passports;


    @OneToMany(mappedBy = "users",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<RequestUsers> requestUsers;

    @ManyToOne(fetch = FetchType.LAZY)
    private Region region;




}
