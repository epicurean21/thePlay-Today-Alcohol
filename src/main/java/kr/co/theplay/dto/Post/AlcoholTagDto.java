package kr.co.theplay.dto.Post;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import kr.co.theplay.common.IconKind;
import kr.co.theplay.domain.post.AlcoholTag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
public class AlcoholTagDto {

    @ApiModelProperty(value="아이콘이름", dataType="String", required=true, example="Drinks/soju/main")
    private String iconName;

    @ApiModelProperty(value="아이름", dataType="String", required=true, example="소비뇽블랑")
    private String name;

    @ApiModelProperty(value="색상번호", dataType="Integer", required=true, example="1")
    private Integer color;

    @ApiModelProperty(value="레시피인지여부", dataType="String", required=true, example="N")
    private String recipeYn;

    @Builder
    public AlcoholTagDto(String iconName, String name, Integer color, String recipeYn){
        this.iconName = iconName;
        this.name = name;
        this.color = color;
        this.recipeYn = recipeYn;
    }

    public AlcoholTag toEntity(){
        return AlcoholTag.builder()
                .iconKind(IconKind.enumOf(iconName))
                .name(name)
                .color(color)
                .recipeYn(recipeYn)
                .build();
    }

    public AlcoholTagDto(AlcoholTag alcoholTag){
        this.iconName = alcoholTag.getIconKind().getCodeValue();
        this.name = alcoholTag.getName();
        this.color = alcoholTag.getColor();
        this.recipeYn = alcoholTag.getRecipeYn();
    }
}
