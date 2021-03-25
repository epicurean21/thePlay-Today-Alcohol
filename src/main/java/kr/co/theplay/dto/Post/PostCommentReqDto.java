package kr.co.theplay.dto.Post;

import io.swagger.annotations.ApiModelProperty;
import kr.co.theplay.domain.post.PostComment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCommentReqDto {
    @ApiModelProperty(value = "댓글 내용", dataType = "String", required = true, example = "이건 댓글 입니다")
    private String content;

    @ApiModelProperty(value = "대댓글의 부모 댓글 아이디", dataType = "Long", required = true, example = "1")
    private Long postCommentParentId;

    @Builder
    public PostCommentReqDto(String content) {
        this.content = content;
    }

    public PostComment toEntity() {
        return PostComment.builder()
                .content(this.content)
                .postCommentParentId(this.postCommentParentId)
                .build();
    }
}
