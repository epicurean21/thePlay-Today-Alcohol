package kr.co.theplay.dto.follow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowUserDto {

    @ApiModelProperty(value = "아이디", dataType = "Long", required = true, example = "1")
    private Long id;

    @ApiModelProperty(value = "닉네임", dataType = "String", required = true, example = "우아한보드카")
    private String nickname;

    @Builder
    public FollowUserDto(Long id, String nickname){
        this.id = id;
        this.nickname = nickname;
    }
}
