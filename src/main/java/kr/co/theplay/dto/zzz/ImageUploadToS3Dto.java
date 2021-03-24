package kr.co.theplay.dto.zzz;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.aspectj.weaver.ast.Test;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ImageUploadToS3Dto {

//    private List<MultipartFile> files;
    private String title;
    private Long id;
    private Integer num;

    private List<TestDto> testDtos;

    private TestDto testDto;

    @Builder
    public ImageUploadToS3Dto( String title, Long id, Integer num,
                              List<TestDto> testDtos, TestDto testDto) {
//        this.files = files;
        this.title = title;
        this.id = id;
        this.num = num;
        this.testDtos = testDtos;
        this.testDto = testDto;
    }
}
