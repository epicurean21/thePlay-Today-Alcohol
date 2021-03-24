package kr.co.theplay.dto.Post;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostReportReqDto {
    @ApiModelProperty(value = "신고 이유", dataType = "String", required = true, example = "스팸")
    private String content;

    @Builder
    public PostReportReqDto(String content) {
        this.content = content;
    }
}
