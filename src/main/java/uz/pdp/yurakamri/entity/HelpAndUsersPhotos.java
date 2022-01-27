package uz.pdp.yurakamri.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.yurakamri.entity.enums.HelpType;

import javax.persistence.*;

@Entity(name = "photo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelpAndUsersPhotos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private long buffer;

    private String photoId;




    @ManyToOne(fetch = FetchType.LAZY)
    private HelpAndUsers helps;





}
