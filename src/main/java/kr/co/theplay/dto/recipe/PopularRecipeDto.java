package kr.co.theplay.dto.recipe;

import kr.co.theplay.domain.post.AlcoholTag;
import kr.co.theplay.domain.post.Post;
import kr.co.theplay.domain.post.PostImage;
import kr.co.theplay.dto.post.RecipeIngredientDto;
import kr.co.theplay.service.zzz.S3Service;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PopularRecipeDto {

    private String alcoholTagName;

    private List<RecipeIngredientDto> ingredients;

    private List<String> images;

    @Builder
    public PopularRecipeDto(String alcoholTagName, List<RecipeIngredientDto> ingredients, List<String> images){
        this.alcoholTagName = alcoholTagName;
        this.ingredients = ingredients;
        this.images = images;
    }

}
