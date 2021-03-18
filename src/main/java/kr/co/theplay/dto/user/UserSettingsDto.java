package kr.co.theplay.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSettingsDto {
    @ApiModelProperty(value = "닉네임", dataType = "String", required = true, example = "오늘한주")
    private String nickname;
    @ApiModelProperty(value = "이메일", dataType = "String", required = true, example = "sample@naver.com")
    private String email;

    @Builder
    public UserSettingsDto(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }
}
