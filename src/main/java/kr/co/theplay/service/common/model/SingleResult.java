package kr.co.theplay.service.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleResult<T> extends CommonResult {

    @ApiModelProperty(value = "조회 데이터", dataType = "Object", required = true)
    private T data;
}
