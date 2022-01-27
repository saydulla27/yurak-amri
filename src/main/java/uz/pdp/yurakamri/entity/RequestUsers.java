package uz.pdp.yurakamri.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import uz.pdp.yurakamri.entity.enums.HelpType;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private long buffer;

    @Enumerated(EnumType.STRING)
    private HelpType helpType;

    private String date;
    private String description;
    private boolean status;
    private boolean answer;
    private boolean active;



    @ManyToOne
    private Ketmon users;

    @ManyToOne(fetch = FetchType.LAZY)
    private Region region;




}
