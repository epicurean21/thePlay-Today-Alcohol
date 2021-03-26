package kr.co.theplay.dto.post;

import io.swagger.annotations.ApiModelProperty;
import kr.co.theplay.domain.post.PostImage;
import kr.co.theplay.service.zzz.S3Service;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostImageDto {

    @ApiModelProperty(value = "이미지번호", dataType = "Integer" ,required = true, example = "0")
    private Integer number;

    @ApiModelProperty(value = "이미지경로", dataType = "String", required = true, example = "https://cloud~~")
    private String filePath;

    @Builder
    public PostImageDto(Integer number, String filePath){
        this.number = number;
        this.filePath = filePath;
    }

    public PostImageDto(PostImage postImage){
        this.number = postImage.getNumber();
        this.filePath =  "https://" + S3Service.CLOUD_FRONT_DOMAIN_NAME + "/" + postImage.getFilePath();
    }
}
