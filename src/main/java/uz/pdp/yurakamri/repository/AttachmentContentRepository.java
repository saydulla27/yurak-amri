package uz.pdp.yurakamri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.yurakamri.entity.AttachmentContent;

import java.util.Optional;

public interface AttachmentContentRepository extends JpaRepository<AttachmentContent,Integer> {
    Optional<AttachmentContent> findByAttachmentId(Integer integer);
}
