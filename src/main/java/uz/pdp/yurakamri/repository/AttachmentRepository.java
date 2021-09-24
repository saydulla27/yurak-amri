package uz.pdp.yurakamri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.yurakamri.entity.Attachment;

public interface AttachmentRepository  extends JpaRepository<Attachment,Integer> {
}
