package uz.pdp.yurakamri.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import uz.pdp.yurakamri.entity.enums.HelpType;
import uz.pdp.yurakamri.entity.enums.Role;

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

    @Enumerated
    private Role role;

    private long chatId;

    private String state, fullName, phoneNumber;
    private Integer age, childrenCount;
    private Float lat, lon;

    @CreationTimestamp
    private Date date;

    @OneToOne
    private Attachment attachment;

}
