package kr.co.theplay.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserMainInfoDto {
    @ApiModelProperty(value = "닉네임", dataType = "Long", required = true, example = "우아한 보드카")
    private String nickname;

    @ApiModelProperty(value = "게시물 수", dataType = "Long", required = true, example = "233")
    private Long posts;

    @ApiModelProperty(value = "좋아요 수", dataType = "Long", required = true, example = "2333")
    private Long likes;

    @ApiModelProperty(value = "팔로워 수", dataType = "Long", required = true, example = "23000")
    private Long followers;

    @ApiModelProperty(value = "나의 레시피 수", dataType = "Long", required = true, example = "1")
    private Long recipes;

    @ApiModelProperty(value = "팔로잉 여부", dataType = "String", required = true, example = "Y")
    private String followingYn;

    @Builder
    public UserMainInfoDto(String nickname, Long posts, Long likes, Long followers, Long recipes, String followingYn) {
        this.nickname = nickname;
        this.posts = posts;
        this.likes = likes;
        this.followers = followers;
        this.recipes = recipes;
        this.followingYn = followingYn;
    }
}
