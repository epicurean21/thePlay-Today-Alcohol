package kr.co.theplay.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserChangePrivacyResDto {
    @ApiModelProperty(value = "공개/비공개 여부", dataType = "String", required = true, example = "Y")
    private String privacyYn;

    @Builder
    public UserChangePrivacyResDto(String privacyYn) {
        this.privacyYn = privacyYn;
    }
}
