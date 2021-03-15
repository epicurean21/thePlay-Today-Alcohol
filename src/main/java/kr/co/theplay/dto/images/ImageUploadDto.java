package kr.co.theplay.dto.images;

import io.swagger.annotations.ApiModelProperty;
import kr.co.theplay.domain.images.Image;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ImageUploadDto {
    @ApiModelProperty(value="아이디", dataType="Long", required=false, example="1")
    private Long id;

    @ApiModelProperty(value="이미지 경로", dataType="String", required=false, example="test.jpeg")
    private String filePath;

    @ApiModelProperty(value="이미지 전체 경로", dataType="String", required=false, example="cloudFronttest.jpeg")
    private String imageFullPath;

    public Image toEntity() {
        Image build = Image.builder()
                .id(id)
                .filePath(filePath)
                .build();
        return build;
    }

    @Builder
    public ImageUploadDto(Long id, String filePath, String imageFullPath) {
        this.id = id;
        this.filePath = filePath;
        this.imageFullPath = imageFullPath;
    }
}
