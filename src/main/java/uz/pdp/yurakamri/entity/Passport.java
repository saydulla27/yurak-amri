package uz.pdp.yurakamri.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private long buffer;
    private String photoId;




    @ManyToOne(fetch = FetchType.LAZY)
    private Ketmon users;

}
