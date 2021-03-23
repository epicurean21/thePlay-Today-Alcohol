package kr.co.theplay.dto.zzz;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ImageUploadToS3Dto {

    private List<MultipartFile> files;
    private String title;
    private Long id;
    private Integer num;

    @Builder
    public ImageUploadToS3Dto(List<MultipartFile> files, String title, Long id, Integer num) {
        this.files = files;
        this.title = title;
        this.id = id;
        this.num = num;
    }
}
