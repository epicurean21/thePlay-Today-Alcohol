package kr.co.theplay.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class UserChangePasswordDto {
    @ApiModelProperty(value = "비밀번호", dataType = "String", required = true, example = "aaaa1234")
    @Pattern(regexp = "[a-zA-Z1-9]{6,12}", message = "비밀번호는 영어와 숫자로 포함해서 6~12자리 이내로 입력해주세요.")
    private String password;

    @ApiModelProperty(value = "비밀번호 확인", dataType = "String", required = true, example = "aaaa1234")
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    private String confirmPassword;

    @Builder
    public UserChangePasswordDto(String password, String confirmPassword) {
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
