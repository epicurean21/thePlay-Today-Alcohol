package kr.co.theplay.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserAlarmNewYnDto {

    @ApiModelProperty(value = "새 알람 존재 여부", dataType = "String", required = true, example = "Y")
    private String newAlarmYn;

    @Builder
    public UserAlarmNewYnDto (String newAlarmYn){
        this.newAlarmYn = newAlarmYn;
    }
}
