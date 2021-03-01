package kr.co.theplay.dto.zzz;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ZUserReqDto {

    @ApiModelProperty(value = "이름", dataType = "String", required = true, example = "haerim")
    private String name;

    @ApiModelProperty(value = "아이디", dataType = "String", required = true, example = "ehl3288@naver.com")
    private String uid;

    @ApiModelProperty(value = "연락처", dataType = "String", required = true, example = "010-5768-5933")
    private String phoneNumber;

    @ApiModelProperty(value = "성별", dataType = "String", required = true, example = "F")
    private String sex;

    @Builder
    public ZUserReqDto(String name, String uid, String phoneNumber, String sex){
        this.name = name;
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.sex = sex;
    }
}
