package kr.co.theplay.dto.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostLikeChangeResDto {
    @ApiModelProperty(value = "좋아요/좋아요 취소", dataType = "String", required = true, example = "Y")
    private String likeYn;

    @Builder
    public PostLikeChangeResDto(String likeYn) {
        this.likeYn = likeYn;
    }
}
