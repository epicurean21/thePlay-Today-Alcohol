package kr.co.theplay.dto.images;

import kr.co.theplay.domain.images.Image;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ImageUploadDto {
    private Long id;
    private String filePath;
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
