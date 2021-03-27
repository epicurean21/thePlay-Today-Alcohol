package kr.co.theplay.dto.recipe;

import io.swagger.annotations.ApiModelProperty;
import kr.co.theplay.domain.post.Post;
import kr.co.theplay.domain.user.UserRecipe;
import kr.co.theplay.dto.post.AlcoholTagDto;
import kr.co.theplay.dto.post.RecipeIngredientDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserRecipeResDto {
    // 사용자가 저장한 레시피 목록 Response Dto
    @ApiModelProperty(value = "게시글 아이디", dataType = "Long", required = true, example = "1")
    private Long postId;

    @ApiModelProperty(value = "술 태그", dataType = "List", required = true)
    private AlcoholTagDto alcoholTag;

    @ApiModelProperty(value = "재료", dataType = "List", required = true)
    private List<RecipeIngredientDto> ingredients;

    @Builder
    public UserRecipeResDto(Long postId, AlcoholTagDto alcoholTag, List<RecipeIngredientDto> ingredients) {
        this.postId = postId;
        this.alcoholTag = alcoholTag;
        this.ingredients = ingredients;
    }

    public UserRecipeResDto(UserRecipe userRecipe) {
        this.postId = userRecipe.getId();
    }
}
