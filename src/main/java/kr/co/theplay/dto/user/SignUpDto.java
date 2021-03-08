package kr.co.theplay.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;


@Getter
@NoArgsConstructor
public class SignUpDto {

    @ApiModelProperty(value = "이메일", dataType = "String", required = true, example = "sample@sample.com")
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식을 지켜주세요")
    private String email;

    @ApiModelProperty(value = "비밀번호", dataType = "String", required = true, example = "password")
    @Pattern(regexp = "[a-zA-Z1-9]{6,12}", message = "비밀번호는 영어와 숫자로 포함해서 6~12자리 이내로 입력해주세요.")
    private String password;

    @ApiModelProperty(value="비밀번호 확인", dataType = "String", required = true, example = "1234")
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    private String confirmPassword;

    @ApiModelProperty(value = "닉네임", dataType = "String", required = true, example = "오늘 한 주")
    @NotBlank(message = "닉네임을 입력해주세요")
    private String nickname;

    @Builder
    public SignUpDto(String email, String password, String confirmPassword, String nickname) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.nickname = nickname;
    }

    public void encodePassword(String encodePassword){
        this.password = password;
    }
}
