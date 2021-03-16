package kr.co.theplay.dto.images;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ImageUploadToS3Dto {
    @ApiModelProperty(value = "파일", dataType = "MultipartFile", required = true, example = "file1.jpeg")
    private MultipartFile[] file;

    @Builder
    public ImageUploadToS3Dto(MultipartFile[] file) {
        this.file = file;
    }
}
