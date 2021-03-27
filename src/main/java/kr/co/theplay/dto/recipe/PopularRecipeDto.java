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

    public static PopularRecipeDto toDto(Post post){

        //recipe의 alcoholName 세팅
        List<AlcoholTag> alcoholTags = post.getAlcoholTags();
        String tagName = null;
        for(int i = 0; i< alcoholTags.size(); i++){
            if(alcoholTags.get(i).getRecipeYn().equals("Y")){
                tagName = alcoholTags.get(i).getName();
            }
        }

        return PopularRecipeDto.builder().postId(post.getId()).alcoholTagName(tagName).build();
    }

}
