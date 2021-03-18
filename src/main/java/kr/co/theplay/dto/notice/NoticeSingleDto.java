package kr.co.theplay.dto.notice;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoticeSingleDto {
    @ApiModelProperty(value = "아이디", dataType = "Long", required = true, example = "1")
    private Long id;

    @ApiModelProperty(value = "제목", dataType = "String", required = true, example = "오늘 한 주 공지사항 1")
    private String title;

    @ApiModelProperty(value = "제목", dataType = "String", required = true, example = "공지사항 내용입니다")
    private String content;

    @ApiModelProperty(value = "작성 시간", dataType = "String", required = true, example = "2021-03-18T18:38:28")
    private LocalDateTime writtenAt;

    @Builder
    public NoticeSingleDto(Long id, String title, String content, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writtenAt = lastModifiedDate;
    }
}
