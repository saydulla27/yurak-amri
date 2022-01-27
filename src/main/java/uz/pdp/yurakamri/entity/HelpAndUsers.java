package uz.pdp.yurakamri.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.yurakamri.entity.enums.HelpType;

import javax.persistence.*;
import java.util.List;

@Entity(name = "helps")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelpAndUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private long buffer;

    @Enumerated(EnumType.STRING)
    private HelpType helpType;

    private String date;
    private String admin;




    @ManyToOne(fetch = FetchType.LAZY)
    private Ketmon users;

    @JsonIgnore
    @OneToMany(mappedBy = "helps",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<HelpAndUsersPhotos> helpAndUsersPhotos;



}
