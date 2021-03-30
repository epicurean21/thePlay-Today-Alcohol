package kr.co.theplay.dto.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentLikeResDto {
    @ApiModelProperty(value = "좋아요 / 좋아요 취소", dataType = "String", required = true, example = "Y")
    private String likeYn;

    @Builder
    public CommentLikeResDto(String likeYn) {
        this.likeYn = likeYn;
    }
}
