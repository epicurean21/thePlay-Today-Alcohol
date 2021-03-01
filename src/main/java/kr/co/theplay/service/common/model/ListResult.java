package kr.co.theplay.service.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListResult<T> extends CommonResult {

    @ApiModelProperty(value = "조회 데이터 List", dataType = "List", required = true)
    private List<T> list;
}
