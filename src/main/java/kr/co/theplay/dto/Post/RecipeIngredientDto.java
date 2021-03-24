package kr.co.theplay.dto.Post;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import kr.co.theplay.common.IconKind;
import kr.co.theplay.domain.post.RecipeIngredient;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecipeIngredientDto {

    @ApiModelProperty(value="아이콘이름", dataType="String", required=true, example="Drinks/soju/main")
    private String iconName;

    @ApiModelProperty(value="재료이름", dataType="String", required=true, example="레몬")
    private String name;

    @ApiModelProperty(value = "재료 수량", dataType = "String", required = true, example = "30%")
    private String quantity;

    @ApiModelProperty(value="색상번호", dataType="Integer", required=true, example="1")
    private Integer color;

    @Builder
    public RecipeIngredientDto(String iconName, String name, String quantity, Integer color){
        this.iconName = iconName;
        this.name = name;
        this.quantity = quantity;
        this.color = color;
    }

    public RecipeIngredient toEntity(){
        return RecipeIngredient.builder()
                .iconKind(IconKind.enumOf(iconName))
                .name(name)
                .quantity(quantity)
                .color(color)
                .build();
    }
}
