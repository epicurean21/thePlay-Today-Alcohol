package kr.co.theplay.dto.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageDto {

    @ApiModelProperty(value = "페이지 번호", dataType = "int", required = true, example = "1")
    private int pageNumber;

    @ApiModelProperty(value = "페이지 크기", dataType = "int", required = true, example = "5")
    private int pageSize;

    @Builder
    public PageDto(int pageNumber, int pageSize){
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
}
