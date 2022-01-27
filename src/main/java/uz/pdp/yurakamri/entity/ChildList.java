package uz.pdp.yurakamri.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private long buffer;
    private String sex;
    private String name;
    private int age;



    @ManyToOne(fetch = FetchType.LAZY)
    private Ketmon users;

}
