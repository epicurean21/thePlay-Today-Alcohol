package kr.co.theplay.dto.post;

import io.swagger.annotations.ApiModelProperty;
import kr.co.theplay.domain.post.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostReqDto {

    @ApiModelProperty(value="게시글 내용", dataType="String", required=true, example="오늘은 오랜만에 보드카 마신 날~")
    private String content;

    @ApiModelProperty(value = "레시피 존재 여부", dataType = "String", required = true, example = "Y")
    private String haveRecipeYn;

    @ApiModelProperty(value = "술 태그들", dataType = "List", required = true)
    private List<AlcoholTagDto> alcoholTags;

    @ApiModelProperty(value = "재료들", dataType = "List", required = true)
    private List<RecipeIngredientDto> ingredients;

    @ApiModelProperty(value = "레시피 단계들", dataType = "List", required = true)
    private List<RecipeStepDto> steps;

    @Builder
    public PostReqDto(String content, String haveRecipeYn, List<AlcoholTagDto> alcoholTags, List<RecipeIngredientDto> ingredients,
                      List<RecipeStepDto> steps){
        this.content = content;
        this.haveRecipeYn = haveRecipeYn;
        this.alcoholTags = alcoholTags;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public Post toEntity(){
        return Post.builder()
                .content(content)
                .haveRecipeYn(haveRecipeYn)
                .build();
    }

}
