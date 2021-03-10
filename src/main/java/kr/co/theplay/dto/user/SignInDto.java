package kr.co.theplay.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInDto {

    @ApiModelProperty(value="이메일", dataType="String", required=true, example="hdmedi@hdmedi.co.kr")
    private String email;
    @ApiModelProperty(value="비밀번호", dataType="String", required=true, example="1234")
    private String password;

    @Builder
    public SignInDto(String email, String password){
        this.email = email;
        this.password = password;
    }
}
