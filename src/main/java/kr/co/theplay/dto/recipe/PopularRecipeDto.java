package kr.co.theplay.dto.recipe;

import kr.co.theplay.dto.post.RecipeIngredientDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PopularRecipeDto {

    private Long postId;

    private String alcoholTagName;

    private List<RecipeIngredientDto> ingredients;

    private List<String> images;

    @Builder
    public PopularRecipeDto(Long postId, String alcoholTagName, List<RecipeIngredientDto> ingredients, List<String> images){
        this.postId = postId;
        this.alcoholTagName = alcoholTagName;
        this.ingredients = ingredients;
        this.images = images;
    }
}
