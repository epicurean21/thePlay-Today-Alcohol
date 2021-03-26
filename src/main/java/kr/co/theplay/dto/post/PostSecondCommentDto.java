package kr.co.theplay.dto.post;

import io.swagger.annotations.ApiModelProperty;
import kr.co.theplay.domain.post.PostComment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostSecondCommentDto {

    @ApiModelProperty(value = "댓글 아이디", dataType = "Long", required = true, example = "1")
    private Long postCommentId;

    @ApiModelProperty(value = "유저아이디", dataType = "Long", required = true, example = "1")
    private Long userId;

    @ApiModelProperty(value = "유저닉네임", dataType = "String", required = true, example = "신나는칵테일녀")
    private String nickname;

    @ApiModelProperty(value = "댓글 내용", dataType = "String", required = true, example = "이건 대댓글 입니다")
    private String content;

    @ApiModelProperty(value = "좋아요 여부", dataType = "String", required = true, example = "Y")
    private String commentLikeYn;

    @ApiModelProperty(value = "좋아요 개수", dataType = "Long", required = true, example = "2")
    private Long commentLikeCount;

    @Builder
    public PostSecondCommentDto(Long postCommentId, Long userId, String nickname, String content, String commentLikeYn, Long commentLikeCount) {
        this.postCommentId = postCommentId;
        this.userId = userId;
        this.nickname = nickname;
        this.content = content;
        this.commentLikeCount = commentLikeCount;
    }

    public PostSecondCommentDto(PostComment postComment) {
        this.postCommentId = postComment.getId();
        this.userId = postComment.getUser().getId();
        this.nickname = postComment.getUser().getNickname();
        this.content = postComment.getContent();
    }
}
