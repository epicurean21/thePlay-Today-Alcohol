package kr.co.theplay.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoDto {

    @ApiModelProperty(value = "아이디", dataType = "Long", required = true, example = "1")
    private Long id;

    @ApiModelProperty(value = "닉네임", dataType = "String", required = true, example = "우아한보드카")
    private String nickname;

    @ApiModelProperty(value = "이메일", dataType = "String", required = true, example = "sample@sample.com")
    private String email;

    @Builder
    public UserInfoDto(Long id, String nickname, String email){
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }
}
