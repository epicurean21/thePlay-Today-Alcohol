package kr.co.theplay.dto.follow;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlockedUserDto {

    @ApiModelProperty(value = "차단당한 이용자 이름", dataType = "String", required = true, example = "우아한 보드카")
    private String blocked_user_name;

    @Builder
    public BlockedUserDto(String blocked_user_name){
        this.blocked_user_name = blocked_user_name;
    }
}
