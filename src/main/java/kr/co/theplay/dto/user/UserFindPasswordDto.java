package kr.co.theplay.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserFindPasswordDto {
    @ApiModelProperty(value = "이메일", dataType = "String", required = true, example = "test@test.com")
    private String email;

    @Builder
    public UserFindPasswordDto(String email) {
        this.email = email;
    }
}
