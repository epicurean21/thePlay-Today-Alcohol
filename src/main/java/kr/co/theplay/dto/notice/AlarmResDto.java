package kr.co.theplay.dto.notice;

import io.swagger.annotations.ApiModelProperty;
import kr.co.theplay.domain.notice.Alarm;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlarmResDto {
    @ApiModelProperty(value = "알람 아이디", dataType = "Long", required = true, example = "1")
    private Long id;

    @ApiModelProperty(value = "알림 내용", dataType = "String", required = true, example = "OO님이 게시글에 댓글을 남겼습니다")
    private String content;

    @ApiModelProperty(value = "알림 확인 여부", dataType = "String", required = true, example = "Y")
    private String readYn;

    @ApiModelProperty(value = "종류", dataType = "String", required = true, example = "comment/like/follow")
    private String type;

    @Builder
    public AlarmResDto(Long id, String content, String readYn, String type) {
        this.id = id;
        this.content = content;
        this.readYn = readYn;
        this.type = type;
    }

    public Alarm toEntity() {
        return Alarm.builder()
                .id(id)
                .content(content)
                .readYn(readYn)
                .type(type)
                .build();
    }
}
