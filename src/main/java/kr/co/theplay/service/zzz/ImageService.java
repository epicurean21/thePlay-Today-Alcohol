package kr.co.theplay.service.zzz;

import kr.co.theplay.domain.zzz.Image;
import kr.co.theplay.domain.zzz.ImageRepository;
import kr.co.theplay.domain.zzz.PostTest;
import kr.co.theplay.domain.zzz.PostTestRepository;
import kr.co.theplay.dto.zzz.ImageUploadDto;
import kr.co.theplay.dto.zzz.ImageUploadToS3Dto;
import kr.co.theplay.dto.zzz.PostDto;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.advice.exception.CommonNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public void savePostDto(List<MultipartFile> files, ImageUploadToS3Dto imageUploadToS3Dto) throws IOException {

//        List<MultipartFile> files = imageUploadToS3Dto.getFiles();

        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmsss");
        String thumbnailPath = files.get(0).getOriginalFilename()+"-" + date.format(new Date());

        PostTest postTest = PostTest.builder().id(imageUploadToS3Dto.getId()).num(imageUploadToS3Dto.getNum()).title(imageUploadToS3Dto.getTitle()).thumbnailPath(thumbnailPath).build();
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

    public PostDto getImage() {
        PostTest postTest = postTestRepository.findById(3L).orElseThrow(
                () -> new CommonNotFoundException("not")
        );
        List<Image> images = imageRepository.findByPostTestId(postTest.getId());
        List<String> paths = new ArrayList<>();
        images.forEach(e -> paths.add(
                "https://" + S3Service.CLOUD_FRONT_DOMAIN_NAME + "/" + e.getFilePath()
        ));

        PostDto dto = PostDto.builder().filePaths(paths).title(postTest.getTitle()).build();
        return dto;
    }

    @Transactional
    public void deleteImages() {
        PostTest postTest = postTestRepository.findById(2L).orElseThrow(
                () -> new CommonNotFoundException("not")
        );
        postTestRepository.delete(postTest);
        List<Image> images = imageRepository.findByPostTestId(postTest.getId());
        images.forEach(e -> {
            try {
                s3Service.delete(e.getFilePath());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        images.forEach(e -> imageRepository.delete(e));

        }
}
