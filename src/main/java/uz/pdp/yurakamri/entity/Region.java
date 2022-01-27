package uz.pdp.yurakamri.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;



    @OneToMany(mappedBy = "region",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<City> cityList;

    @OneToMany(mappedBy = "region",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<RequestUsers> requestUsers;

    @OneToMany(mappedBy = "region",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List <Ketmon> users;

}
