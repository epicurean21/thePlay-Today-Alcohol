package kr.co.theplay.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RandomNicknameDto {
    @ApiModelProperty(value = "닉네임", dataType = "String", required = true, example = "우아한 보드카")
    private String nickname;

    @Builder
    public RandomNicknameDto(String nickname) {
        this.nickname = nickname;
    }
}
