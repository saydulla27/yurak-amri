package uz.pdp.yurakamri.payload;

import lombok.Data;
import uz.pdp.yurakamri.entity.enums.HelpType;

import java.util.List;

@Data
public class AnnouncementDto {
    private Integer ketmonId;
    private String description,status;
    private List<String> helpTypeList;
}
