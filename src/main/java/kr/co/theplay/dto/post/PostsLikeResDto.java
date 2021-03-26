package kr.co.theplay.dto.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostsLikeResDto {
    @ApiModelProperty(value = "게시글 아이디", dataType = "Long", required = true, example = "1")
    private Long postId;

    @ApiModelProperty(value = "유저아이디", dataType = "Long", required = true, example = "1")
    private Long userId;

    @ApiModelProperty(value = "유저닉네임", dataType = "String", required = true, example = "신나는칵테일녀")
    private String nickname;

    @ApiModelProperty(value = "게시물 좋아요 여부", dataType = "String", required = true, example = "Y")
    private String postLikeYn;

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
}
