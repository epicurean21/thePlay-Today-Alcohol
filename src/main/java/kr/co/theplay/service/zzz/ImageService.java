package kr.co.theplay.service.zzz;

import kr.co.theplay.domain.images.Image;
import kr.co.theplay.domain.images.ImageRepository;
import kr.co.theplay.dto.zzz.ImageUploadDto;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ImageService {
    private ImageRepository imageRepository;

    @Transactional
    public void saveImage(ImageUploadDto imageUploadDto) {
        imageRepository.save(imageUploadDto.toEntity());
    }

    @Transactional
    public void editImage(ImageUploadDto imageUploadDto) throws IOException {
        deleteImage(imageUploadDto.getFilePath());
        imageRepository.save(imageUploadDto.toEntity());
    }

    @Transactional
    public void deleteImage(String filePath) throws IOException {
        Image image = imageRepository.findByFilePath(filePath)
                .orElseThrow(() -> new CommonBadRequestException("imageNotFound"));
        imageRepository.delete(image);
    }

    private ImageUploadDto convertEntityToDto(Image image) {
        return ImageUploadDto.builder()
                .id(image.getId())
                .filePath(image.getFilePath())
                .imageFullPath("https://" + S3Service.CLOUD_FRONT_DOMAIN_NAME + "/" + image.getFilePath())
                .build();
    }
}
