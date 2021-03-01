package kr.co.theplay.service.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResult {

    @ApiModelProperty(value = "성공여부", dataType = "Boolean", required = true, example = "true")
    private boolean success;

    @ApiModelProperty(value = "성공여부 코드", dataType = "int", required = true, example = "0")
    private int code;

    @ApiModelProperty(value = "성공여부 메시지", dataType = "String", required = true, example = "성공하였습니다.")
    private String msg;

}
