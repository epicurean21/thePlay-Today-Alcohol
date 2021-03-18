package kr.co.theplay.dto.notice;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeListDto {
    @ApiModelProperty(value = "아이디", dataType = "Long", required = true, example = "1")
    private Long id;

    @ApiModelProperty(value = "제목", dataType = "String", required = true, example = "오늘 한 주 공지사항 1")
    private String title;

    @Builder
    public NoticeListDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
