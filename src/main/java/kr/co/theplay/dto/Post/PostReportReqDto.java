package kr.co.theplay.dto.Post;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostReportReqDto {
    @ApiModelProperty(value = "게시글 번호", dataType = "Integer", required = true, example = "1")
    private Long postId;

    @ApiModelProperty(value = "신고 이유", dataType = "String", required = true, example = "스팸: 0, 선정적: 1, 부적절: 2 ")
    private String content;

    @Builder
    public PostReportReqDto(Long postId, String content) {
        this.postId = postId;
        this.content = content;
    }
}
