package kr.co.theplay.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserMainInfoDto {
    @ApiModelProperty(value = "게시물 수", dataType = "Long", required = true, example = "233")
    private Long posts;

    @ApiModelProperty(value = "좋아요 수", dataType = "Long", required = true, example = "2333")
    private Long likes;

    @ApiModelProperty(value = "팔로워 수", dataType = "Long", required = true, example = "23000")
    private Long followers;

    @ApiModelProperty(value = "나의 레시피 수", dataType = "Long", required = true, example = "1")
    private Long recipes;

    @Builder
    public UserMainInfoDto(Long posts, Long likes, Long followers, Long recipes) {
        this.posts = posts;
        this.likes = likes;
        this.followers = followers;
        this.recipes = recipes;
    }
}
