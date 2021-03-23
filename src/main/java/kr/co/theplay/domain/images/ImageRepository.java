package kr.co.theplay.domain.images;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByFilePath(String filePath);

    List<Image> findByPostTestId(Long id);
}
