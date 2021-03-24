package kr.co.theplay.dto.Post;

import io.swagger.annotations.ApiModelProperty;
import kr.co.theplay.domain.post.RecipeStep;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecipeStepDto {

    @ApiModelProperty(value="스텝번호", dataType="Integer", required=true, example="1")
    private Integer number;

    @ApiModelProperty(value="내용", dataType="String", required=true, example="착즙기를 사용해서 레몬즙을 낸다.")
    private String content;

    @Builder
    public RecipeStepDto(Integer number, String content){
        this.number = number;
        this.content = content;
    }

    public RecipeStep toEntity(){
        return RecipeStep.builder()
                .number(number)
                .content(content)
                .build();
    }

    public RecipeStepDto (RecipeStep recipeStep){
        this.number = recipeStep.getNumber();
        this.content = recipeStep.getContent();
    }
}
