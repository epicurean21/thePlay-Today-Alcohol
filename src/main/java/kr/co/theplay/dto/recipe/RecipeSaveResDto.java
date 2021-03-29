package kr.co.theplay.dto.recipe;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecipeSaveResDto {
    @ApiModelProperty(value = "저장/저장 취소", dataType = "String", required = true, example = "Y")
    private String saveYn;

    @Builder
    public RecipeSaveResDto(String saveYn) {
        this.saveYn = saveYn;
    }
}
