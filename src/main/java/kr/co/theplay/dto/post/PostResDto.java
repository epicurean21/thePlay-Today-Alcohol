package kr.co.theplay.dto.post;

import io.swagger.annotations.ApiModelProperty;
import kr.co.theplay.domain.post.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostResDto {

    @ApiModelProperty(value = "게시글 아이디", dataType = "Long", required = true, example = "1")
    private Long postId;

    @ApiModelProperty(value = "유저아이디", dataType = "Long", required = true, example = "1")
    private Long userId;

    @ApiModelProperty(value = "유저닉네임", dataType = "String", required = true, example = "신나는칵테일녀")
    private String nickname;

    @ApiModelProperty(value = "게시글 내용", dataType = "String", required = true, example = "오늘은 오랜만에 보드카 마신 날~")
    private String content;

    @ApiModelProperty(value = "레시피 존재 여부", dataType = "String", required = true, example = "Y")
    private String haveRecipeYn;

    @ApiModelProperty(value = "게시물 좋아요 여부", dataType = "String", required = true, example = "Y")
    private String postLikeYn;

    @ApiModelProperty(value = "게시글 레시피 저장 여부", dataType = "String", required = true, example = "Y")
    private String saveRecipeYn;

    @ApiModelProperty(value = "게시글 댓글 개수", dataType = "Integer", required = true, example = "2")
    private Long commentCnt;

    @ApiModelProperty(value = "게시글 대표 댓글", dataType = "String", required = true, example = "게시글의 댓글입니다.")
    private String comment;

    @ApiModelProperty(value = "생성일자", dataType = "LocalDateTime", required = true)
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "이미지들", dataType = "List", required = true)
    private List<PostImageDto> images;

    @ApiModelProperty(value = "술 태그들", dataType = "List", required = true)
    private List<AlcoholTagDto> alcoholTags;

    @ApiModelProperty(value = "재료들", dataType = "List", required = true)
    private List<RecipeIngredientDto> ingredients;

    @ApiModelProperty(value = "레시피 단계들", dataType = "List", required = true)
    private List<RecipeStepDto> steps;

    @Builder
    public PostResDto(Long postId, Long userId, String nickname, String content, LocalDateTime createdDate,
                      String haveRecipeYn, String postLikeYn, String saveRecipeYn, Long commentCnt, String comment, List<PostImageDto> images, List<AlcoholTagDto> alcoholTags,
                      List<RecipeIngredientDto> ingredients, List<RecipeStepDto> steps) {
        this.postId = postId;
        this.userId = userId;
        this.nickname = nickname;
        this.content = content;
        this.haveRecipeYn = haveRecipeYn;
        this.saveRecipeYn = saveRecipeYn;
        this.postLikeYn = postLikeYn;
        this.commentCnt = commentCnt;
        this.comment = comment;
        this.createdDate = createdDate;
        this.images = images;
        this.alcoholTags = alcoholTags;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public PostResDto(Post post) {
        this.postId = post.getId();
        this.userId = post.getUser().getId();
        this.nickname = post.getUser().getNickname();
        this.content = post.getContent();
        this.haveRecipeYn = post.getHaveRecipeYn();
        this.createdDate = post.getCreatedDate();
    }
}
