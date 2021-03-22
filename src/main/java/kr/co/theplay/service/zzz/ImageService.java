package kr.co.theplay.service.zzz;

import kr.co.theplay.domain.images.Image;
import kr.co.theplay.domain.images.ImageRepository;
import kr.co.theplay.domain.images.PostTest;
import kr.co.theplay.domain.images.PostTestRepository;
import kr.co.theplay.dto.zzz.ImageUploadDto;
import kr.co.theplay.dto.zzz.ImageUploadToS3Dto;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private ImageRepository imageRepository;
    private PostTestRepository postTestRepository;
    private S3Service s3Service;

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

    @Transactional
    public void savePostDto(ImageUploadToS3Dto imageUploadToS3Dto) throws IOException {

        List<MultipartFile> files = imageUploadToS3Dto.getFiles();

        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmsss");
        String thumbnailPath = files.get(0).getOriginalFilename()+"-" + date.format(new Date());

        PostTest postTest = PostTest.builder().title(imageUploadToS3Dto.getTitle()).thumbnailPath(thumbnailPath).build();
        PostTest savedPost = postTestRepository.save(postTest);

        for(int i = 0; i< files.size(); i++){
            String imagePath = s3Service.upload(files.get(i));
            if(imagePath == "EXCEED") {
                throw new CommonBadRequestException("imageSizeExcessLimit");
            }

            Image image = Image.builder().filePath(imagePath).postTestId(savedPost.getId()).build();
            imageRepository.save(image);
        }

    }

}
