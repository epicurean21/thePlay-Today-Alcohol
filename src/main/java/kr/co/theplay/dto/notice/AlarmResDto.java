package kr.co.theplay.dto.notice;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlarmResDto {
    @ApiModelProperty(value = "아이디", dataType = "Long", required = true, example = "1")
    private Long id;

    
}
