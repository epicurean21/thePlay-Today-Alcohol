package kr.co.theplay.domain.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserFindPasswordDto {
    @ApiModelProperty(value = "이메일", dataType = "String", required = true, example = "test@test.com")
    private String email;

    @ApiModelProperty(value = "비밀번호", dataType = "String", required = true, example = "password")
    private String password;

    @Builder
    public UserFindPasswordDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
